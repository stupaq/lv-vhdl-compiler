package stupaq.vhdl2lv;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stupaq.MissingFeature;
import stupaq.concepts.ComponentBindingResolver;
import stupaq.concepts.InterfaceDeclaration;
import stupaq.labview.scripting.hierarchy.Formula;
import stupaq.labview.scripting.hierarchy.FormulaNode;
import stupaq.labview.scripting.hierarchy.Loop;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.labview.scripting.hierarchy.VI;
import stupaq.labview.scripting.hierarchy.WhileLoop;
import stupaq.lvproject.InstanceName;
import stupaq.lvproject.LVProject;
import stupaq.metadata.ConnectorPaneTerminal;
import stupaq.naming.ArchitectureName;
import stupaq.naming.IOReference;
import stupaq.naming.Identifier;
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
  /** Context of {@link ConcurrentStatementsEmitter}. */
  private boolean applyFallback;
  /** Result. */
  private final WiresBlacklist wiresBlacklist = new WiresBlacklist();
  /** Result. */
  private final StringBuilder fallbackText = new StringBuilder();

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
  public void visit(component_instantiation_statement n) {
    applyFallback = false;
    InstanceName instance = Identifier.instantiation(resolver, architecture,  n.instantiated_unit);
    final InterfaceDeclaration entity = resolver.get(instance.interfaceName());
    Verify.verifyNotNull(entity, "Missing component or entity declaration: %s",
        instance.interfaceName());
    String label = n.instantiation_label.label.representation();
    LOGGER.debug("Instance of: {} labelled: {}", entity.name(), label);
    final UniversalSubVI subVI = new UniversalSubVI(theVi, project, instance, entity, of(label));
    sequence(n.nodeOptional, n.nodeOptional1).accept(new DepthFirstVisitor() {
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
      public void visit(named_association_list n) {
        elementIndex = Integer.MIN_VALUE;
        super.visit(n);
      }

      @Override
      public void visit(positional_association_list n) {
        elementIndex = 0;
        super.visit(n);
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
        Verify.verify(portTerminal == null);
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
        Verify.verify(portTerminal == null);
        ++elementIndex;
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
        super.visit(n);
      }

      @Override
      public void visit(port_map_aspect n) {
        isGenericAspect = false;
        super.visit(n);
      }
    });
  }

  @Override
  public void visit(block_statement n) {
    applyFallback = false;
    MissingFeature.missing("Blocks are not supported at this time.", n);
  }

  @Override
  public void visit(process_statement n) {
    applyFallback = false;
    Loop loop = new WhileLoop(theVi, Optional.<String>absent());
    // Emit process body AND declarations.
    Formula formula =
        new FormulaNode(loop.diagram(), n.representation(), PROCESS_STATEMENT_PART_LABEL);
    // Connect wires.
    new ProcessSignalsEmitter(loop, formula, danglingSinks, namedSources, wiresBlacklist).visit(n);
  }

  @Override
  public void visit(concurrent_procedure_call_statement n) {
    // TODO for now we can rely on fallback
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
  }

  @Override
  public void visit(generate_statement n) {
    // TODO for now we can rely on fallback
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
  public void visit(architecture_statement_part n) {
    n.nodeListOptional.accept(this);
  }
}