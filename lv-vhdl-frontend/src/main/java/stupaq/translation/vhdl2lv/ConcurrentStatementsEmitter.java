package stupaq.translation.vhdl2lv;

import com.google.common.base.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stupaq.labview.hierarchy.Formula;
import stupaq.labview.hierarchy.FormulaNode;
import stupaq.labview.hierarchy.Loop;
import stupaq.labview.hierarchy.Terminal;
import stupaq.labview.hierarchy.VI;
import stupaq.labview.hierarchy.WhileLoop;
import stupaq.translation.errors.MissingFeatureException;
import stupaq.translation.naming.ArchitectureName;
import stupaq.translation.naming.IOReference;
import stupaq.translation.naming.Identifier;
import stupaq.translation.naming.InstantiableName;
import stupaq.translation.project.LVProject;
import stupaq.vhdl93.ast.architecture_statement_part;
import stupaq.vhdl93.ast.association_list;
import stupaq.vhdl93.ast.block_statement;
import stupaq.vhdl93.ast.component_instantiation_statement;
import stupaq.vhdl93.ast.concurrent_assertion_statement;
import stupaq.vhdl93.ast.concurrent_procedure_call_statement;
import stupaq.vhdl93.ast.concurrent_signal_assignment_statement;
import stupaq.vhdl93.ast.concurrent_statement;
import stupaq.vhdl93.ast.conditional_signal_assignment;
import stupaq.vhdl93.ast.expression;
import stupaq.vhdl93.ast.generate_statement;
import stupaq.vhdl93.ast.generic_map_aspect;
import stupaq.vhdl93.ast.name;
import stupaq.vhdl93.ast.port_map_aspect;
import stupaq.vhdl93.ast.process_statement;
import stupaq.vhdl93.ast.selected_signal_assignment;
import stupaq.vhdl93.ast.target;
import stupaq.vhdl93.visitor.DepthFirstVisitor;
import stupaq.vhdl93.visitor.NonTerminalsNoOpVisitor;

import static com.google.common.base.Optional.of;
import static stupaq.translation.errors.LocalisedSemanticException.semanticNotNull;
import static stupaq.vhdl93.ast.Builders.sequence;

class ConcurrentStatementsEmitter extends NonTerminalsNoOpVisitor<Void> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentStatementsEmitter.class);
  /** External context. */
  private final ComponentBindingResolver resolver;
  /** External context. */
  private final LVProject project;
  /** External context. */
  private final VI theVi;
  /** External context. */
  private final IOSources namedSources;
  /** External context. */
  private final IOSinks danglingSinks;
  /** Context of {@link ConcurrentStatementsEmitter}. */
  private final ArchitectureName architecture;
  /** Result. */
  private final WiresBlacklist wiresBlacklist = new WiresBlacklist();
  /** Result. */
  private final StringBuilder fallbackText = new StringBuilder();
  /** Context of {@link ConcurrentStatementsEmitter}. */
  private boolean applyFallback;

  public ConcurrentStatementsEmitter(ComponentBindingResolver resolver, LVProject project,
      ArchitectureName architecture, VI theVi, IOSources namedSources, IOSinks danglingSinks) {
    this.resolver = resolver;
    this.project = project;
    this.architecture = architecture;
    this.theVi = theVi;
    this.namedSources = namedSources;
    this.danglingSinks = danglingSinks;
  }

  public WiresBlacklist wiresBlacklist() {
    return wiresBlacklist;
  }

  public Optional<StringBuilder> fallbackText() {
    return fallbackText.length() > 0 ? of(fallbackText) : Optional.<StringBuilder>absent();
  }

  @Override
  public void visit(architecture_statement_part n) {
    n.nodeListOptional.accept(this);
  }

  @Override
  public void visit(concurrent_statement n) {
    applyFallback = true;
    n.nodeChoice.choice.accept(this);
    if (applyFallback) {
      fallbackText.append(n.representation()).append(System.lineSeparator());
    }
  }

  @Override
  public void visit(component_instantiation_statement n) {
    applyFallback = false;
    InstantiableName instance =
        Identifier.instantiation(resolver.architectures(), architecture, n.instantiated_unit);
    final InterfaceDeclaration entity = resolver.get(instance.interfaceName());
    semanticNotNull(entity, n, "Missing component or entity declaration: %s.",
        instance.interfaceName());
    String label = n.instantiation_label.label.representation();
    LOGGER.debug("Instantiating: {} with label: {}", entity.name(), label);
    final UniversalSubVI subVI = new UniversalSubVI(theVi, project, instance, entity, of(label));
    sequence(n.nodeOptional, n.nodeOptional1).accept(new AssociationListVisitor<Void>() {
      /** Context of {@link #visit(association_list)}. */
      boolean isGenericAspect;

      @Override
      public void visit(generic_map_aspect n) {
        isGenericAspect = true;
        n.association_list.accept(this);
      }

      @Override
      public void visit(port_map_aspect n) {
        isGenericAspect = false;
        n.association_list.accept(this);
      }

      @Override
      protected void actualPartOpen(int elementIndex) {
        // Do not connect.
      }

      @Override
      protected void actualPartOpen(IOReference name) {
        // Do not connect.
      }

      @Override
      protected void actualPartExpression(int elementIndex, expression n) {
        ConnectorPaneTerminal terminal = isGenericAspect ? entity.resolveGeneric(elementIndex)
            : entity.resolvePort(elementIndex);
        semanticNotNull(terminal, n, "Missing terminal for index: {}.", elementIndex);
        assignExpression(subVI.terminal(terminal), terminal.isInput(), n);
      }

      @Override
      protected void actualPartExpression(IOReference name, expression n) {
        ConnectorPaneTerminal terminal =
            isGenericAspect ? entity.resolveGeneric(name) : entity.resolvePort(name);
        semanticNotNull(terminal, n, "Missing terminal for name: {}.", name);
        assignExpression(subVI.terminal(terminal), terminal.isInput(), n);
      }

      private void assignExpression(Terminal terminal, boolean isSink, expression n) {
        if (isSink) {
          new SourceEmitter(theVi, danglingSinks, namedSources).emitWithSink(n, terminal);
        } else {
          new SinkEmitter(theVi, danglingSinks, namedSources).emitWithSource(terminal, n);
        }
      }
    });
  }

  @Override
  public void visit(process_statement n) {
    applyFallback = false;
    Loop loop = new WhileLoop(theVi, Optional.<String>absent());
    // Emit process body AND declarations.
    Formula formula =
        new FormulaNode(loop.diagram(), n.representation(), Optional.<String>absent());
    // Connect wires, emit latches.
    new ProcessSignalsEmitter(loop, formula, danglingSinks, namedSources, wiresBlacklist).visit(n);
    formula.cleanupFormula();
  }

  @Override
  public void visit(block_statement n) {
    applyFallback = false;
    throw new MissingFeatureException(n, "Blocks are not supported at this time.");
  }

  @Override
  public void visit(concurrent_assertion_statement n) {
    applyFallback = false;
    final Formula formula = new FormulaNode(theVi, n.representation(), Optional.<String>absent());
    final SourceEmitter sourceEmitter = new SourceEmitter(theVi, danglingSinks, namedSources);
    n.accept(new DepthFirstVisitor() {
      @Override
      public void visit(expression n) {
        // Note that sine we do not descend recursively, we will cover each expression once.
        sourceEmitter.addTerminals(formula, n);
      }
    });
    formula.cleanupFormula();
  }

  @Override
  public void visit(concurrent_procedure_call_statement n) {
    // We rely on fallback.
  }

  @Override
  public void visit(concurrent_signal_assignment_statement n) {
    applyFallback = false;
    final Formula formula = new FormulaNode(theVi, n.representation(), Optional.<String>absent());
    final SinkEmitter sinkEmitter = new SinkEmitter(theVi, danglingSinks, namedSources);
    final SourceEmitter sourceEmitter = new SourceEmitter(theVi, danglingSinks, namedSources);
    n.accept(new DepthFirstVisitor() {
      @Override
      public void visit(selected_signal_assignment n) {
        n.target.accept(this);
        sourceEmitter.addTerminals(formula, n.expression);
        sourceEmitter.addTerminals(formula, n.selected_waveforms);
      }

      @Override
      public void visit(conditional_signal_assignment n) {
        n.target.accept(this);
        sourceEmitter.addTerminals(formula, n.conditional_waveforms);
      }

      @Override
      public void visit(target n) {
        n.accept(new DepthFirstVisitor() {
          @Override
          public void visit(name n) {
            sinkEmitter.addTerminals(formula, sourceEmitter, n);
          }
        });
      }
    });
    formula.cleanupFormula();
  }

  @Override
  public void visit(generate_statement n) {
    // We rely on fallback.
  }
}
