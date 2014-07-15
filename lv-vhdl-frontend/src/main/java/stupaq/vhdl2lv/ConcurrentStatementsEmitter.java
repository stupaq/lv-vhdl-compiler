package stupaq.vhdl2lv;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stupaq.MissingFeatureException;
import stupaq.SemanticException;
import stupaq.concepts.ComponentBindingResolver;
import stupaq.concepts.ConnectorPaneTerminal;
import stupaq.concepts.InterfaceDeclaration;
import stupaq.labview.hierarchy.Formula;
import stupaq.labview.hierarchy.FormulaNode;
import stupaq.labview.hierarchy.Loop;
import stupaq.labview.hierarchy.Terminal;
import stupaq.labview.hierarchy.VI;
import stupaq.labview.hierarchy.WhileLoop;
import stupaq.naming.ArchitectureName;
import stupaq.naming.IOReference;
import stupaq.naming.Identifier;
import stupaq.naming.InstanceName;
import stupaq.project.LVProject;
import stupaq.vhdl93.ast.*;
import stupaq.vhdl93.visitor.DepthFirstVisitor;
import stupaq.vhdl93.visitor.NonTerminalsNoOpVisitor;

import static com.google.common.base.Optional.of;
import static stupaq.vhdl93.ast.ASTBuilders.sequence;

public class ConcurrentStatementsEmitter extends NonTerminalsNoOpVisitor<Void> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentStatementsEmitter.class);
  private static final Optional<String> PROCESS_STATEMENT_PART_LABEL = of("PROCESS");
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
    InstanceName instance = Identifier.instantiation(resolver, architecture, n.instantiated_unit);
    final InterfaceDeclaration entity = resolver.get(instance.interfaceName());
    entity.materialiseVI(project, namedSources, danglingSinks);
    SemanticException.checkNotNull(entity, n, "Missing component or entity declaration: %s.",
        instance.interfaceName());
    String label = n.instantiation_label.label.representation();
    LOGGER.debug("Instantiating: {} with label: {}", entity.name(), label);
    final UniversalSubVI subVI = new UniversalSubVI(theVi, project, instance, entity, of(label));
    sequence(n.nodeOptional, n.nodeOptional1).accept(new NonTerminalsNoOpVisitor() {
      /**
       * Context of {@link #visit(generic_map_aspect)} and {@link
       * #visit(port_map_aspect)}.
       */
      boolean isGenericAspect;
      /** Context of {@link #visit(positional_association_list)}. */
      int elementIndex;
      /** Context of {@link #visit(actual_part)}. */
      boolean portIsSink;
      /** Context of {@link #visit(actual_part)}. */
      Terminal portTerminal;

      @Override
      public void visit(association_list n) {
        n.nodeChoice.accept(this);
      }

      @Override
      public void visit(named_association_list n) {
        elementIndex = Integer.MIN_VALUE;
        n.named_association_element.accept(this);
        n.nodeListOptional.accept(this);
      }

      @Override
      public void visit(positional_association_list n) {
        elementIndex = 0;
        n.positional_association_element.accept(this);
        n.nodeListOptional.accept(this);
      }

      @Override
      public void visit(named_association_element n) {
        Preconditions.checkState(elementIndex == Integer.MIN_VALUE);
        LOGGER.debug("Port assignment: {}", n.representation());
        IOReference ref = new IOReference(n.formal_part.identifier);
        ConnectorPaneTerminal terminal =
            isGenericAspect ? entity.resolveGeneric(ref) : entity.resolvePort(ref);
        portIsSink = terminal.isInput();
        portTerminal = subVI.terminal(terminal.connectorIndex());
        n.actual_part.accept(this);
        Verify.verify(portTerminal == null, "Omitted port association.");
      }

      @Override
      public void visit(positional_association_element n) {
        Preconditions.checkState(elementIndex >= 0);
        LOGGER.debug("Port assignment: {}", n.representation());
        ConnectorPaneTerminal terminal = isGenericAspect ? entity.resolveGeneric(elementIndex)
            : entity.resolvePort(elementIndex);
        portIsSink = terminal.isInput();
        portTerminal = subVI.terminal(terminal.connectorIndex());
        n.actual_part.accept(this);
        Verify.verify(portTerminal == null, "Omitted port association.");
        ++elementIndex;
      }

      @Override
      public void visit(actual_part n) {
        n.nodeChoice.choice.accept(this);
      }

      @Override
      public void visit(actual_part_open n) {
        // This way we do nothing for <OPEN> ports which is very appropriate.
        portTerminal = null;
      }

      @Override
      public void visit(expression n) {
        Preconditions.checkState(portTerminal != null);
        if (portIsSink) {
          new SourceEmitter(theVi, danglingSinks, namedSources).emitWithSink(n, portTerminal);
        } else {
          new SinkEmitter(theVi, danglingSinks, namedSources).emitWithSource(portTerminal, n);
        }
        portTerminal = null;
        // Note that we do not visit recursively in current setting, so we are sure,
        // that this is the top-level expression context.
      }

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
    });
  }

  @Override
  public void visit(process_statement n) {
    applyFallback = false;
    Loop loop = new WhileLoop(theVi, Optional.<String>absent());
    // Emit process body AND declarations.
    Formula formula =
        new FormulaNode(loop.diagram(), n.representation(), PROCESS_STATEMENT_PART_LABEL);
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
    // TODO for now we can rely on fallback
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
    // TODO for now we can rely on fallback
  }
}
