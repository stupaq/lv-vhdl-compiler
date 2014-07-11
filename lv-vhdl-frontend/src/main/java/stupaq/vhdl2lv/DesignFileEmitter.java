package stupaq.vhdl2lv;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Map.Entry;

import stupaq.MissingFeature;
import stupaq.concepts.EntityDeclaration;
import stupaq.concepts.EntityName;
import stupaq.concepts.IOReference;
import stupaq.concepts.Identifier;
import stupaq.labview.scripting.hierarchy.Formula;
import stupaq.labview.scripting.hierarchy.FormulaNode;
import stupaq.labview.scripting.hierarchy.Loop;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.labview.scripting.hierarchy.WhileLoop;
import stupaq.metadata.ConnectorPaneTerminal;
import stupaq.vhdl2lv.IOSinks.Sink;
import stupaq.vhdl2lv.IOSources.Source;
import stupaq.vhdl2lv.WiringRules.PassLabels;
import stupaq.vhdl93.ast.*;
import stupaq.vhdl93.visitor.DepthFirstVisitor;
import stupaq.vhdl93.visitor.FlattenNestedListsVisitor;
import stupaq.vhdl93.visitor.GJNoArguDepthFirst;

import static com.google.common.base.Optional.of;
import static stupaq.vhdl93.ast.ASTBuilders.sequence;

class DesignFileEmitter extends DepthFirstVisitor {
  private static final Logger LOGGER = LoggerFactory.getLogger(DesignFileEmitter.class);
  private static final Optional<String> ARCHITECTURE_DECLARATIVE_PART_LABEL =
      of("ARCHITECTURE EXTRA DECLARATIONS");
  private static final Optional<String> ARCHITECTURE_STATEMENT_PART_LABEL =
      of("ARCHITECTURE EXTRA BODY STATEMENTS");
  private static final Optional<String> PROCESS_STATEMENT_PART_LABEL = of("PROCESS");
  /** Context of {@link #visit(design_file)}. */
  private final Map<EntityName, EntityDeclaration> knownEntities = Maps.newHashMap();
  /** Context of {@link #visit(design_file)}. */
  private final LVProject project;
  /** Context of {@link #visit(architecture_declaration)}. */
  private UniversalVI currentVi;
  /** Context of {@link #visit(architecture_declaration)}. */
  private IOSources namedSources;
  /** Context of {@link #visit(architecture_declaration)}. */
  private IOSinks danglingSinks;
  /** Context of {@link #visit(architecture_declaration)}. */
  private StringBuilder concurrentStatementFallbacked;
  /** Context of {@link #visit(concurrent_statement)}. */
  private boolean concurrentStatementFallback;

  public DesignFileEmitter(LVProject project) {
    this.project = project;
  }

  private EntityDeclaration resolveEntity(EntityName entityName) {
    EntityDeclaration entity = knownEntities.get(entityName);
    Verify.verifyNotNull(entity, "Unknown entity: %s", entityName);
    return entity;
  }

  @Override
  public void visit(design_file n) {
    n.accept(new FlattenNestedListsVisitor());
    super.visit(n);
  }

  @Override
  public void visit(entity_declaration n) {
    EntityDeclaration entity = new EntityDeclaration(n);
    knownEntities.put(entity.name(), entity);
  }

  @Override
  public void visit(architecture_declaration n) {
    concurrentStatementFallbacked = new StringBuilder();
    namedSources = new IOSources();
    danglingSinks = new IOSinks();
    EntityDeclaration entity = resolveEntity(new EntityName(n.entity_name));
    final Identifier architecture = new Identifier(n.architecture_identifier.identifier);
    LOGGER.debug("Architecture: {} of: {}", architecture, entity.name());
    // Create all generics, ports and eventually the VI itself.
    currentVi = new UniversalVI(project, entity, namedSources, danglingSinks);
    // Emit architecture body.
    n.architecture_statement_part.accept(this);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Named sources:");
      for (Entry<IOReference, Source> entry : namedSources.entries()) {
        LOGGER.debug("\t{}", entry);
      }
      LOGGER.debug("Dangling sinks:");
      for (Entry<IOReference, Sink> entry : danglingSinks.entries()) {
        LOGGER.debug("\t{}", entry);
      }
    }
    // Emit entries from architecture declarative part.
    final StringBuilder declarativePartFallbacked = new StringBuilder();
    n.architecture_declarative_part.accept(new DepthFirstVisitor() {
      /** Context of {@link #visit(block_declarative_item)}. */
      boolean declarativePartFallback;

      @Override
      public void visit(constant_declaration n) {
        declarativePartFallback = false;
        IOReference ref = new IOReference(n.identifier_list.identifier);
        Verify.verify(n.nodeOptional.present(), "Missing value for constant: %s", ref);
        // There will be no more dangling sinks than we see right now, we can connect this
        // constant to every pending sink and forget about it.
        final String label = sequence(n.nodeToken, n.identifier_list, n.nodeToken1,
            n.subtype_indication).representation();
        Terminal terminal = n.nodeOptional.accept(new GJNoArguDepthFirst<Terminal>() {
          @Override
          public Terminal visit(NodeSequence n) {
            return n.nodes.get(1).accept(this);
          }

          @Override
          public Terminal visit(expression n) {
            return new SourceEmitter(currentVi, danglingSinks, namedSources).emitAsConstant(n,
                of(label));
          }
        });
        Verify.verify(!namedSources.containsKey(ref), "Constant: %s has other sources", ref);
        namedSources.put(ref, terminal);
      }

      @Override
      public void visit(block_declarative_item n) {
        declarativePartFallback = true;
        super.visit(n);
        if (declarativePartFallback) {
          declarativePartFallbacked.append(n.representation()).append(System.lineSeparator());
        }
      }
    });
    // Emit declarative part leftovers.
    if (declarativePartFallbacked.length() > 0) {
      new FormulaNode(currentVi, declarativePartFallbacked.toString(),
          ARCHITECTURE_DECLARATIVE_PART_LABEL);
    }
    // Emit statement part leftovers.
    if (concurrentStatementFallbacked.length() > 0) {
      new FormulaNode(currentVi, concurrentStatementFallbacked.toString(),
          ARCHITECTURE_STATEMENT_PART_LABEL);
    }
    // All references and labels are resolved now.
    new WiringRules(currentVi, namedSources, danglingSinks, new PassLabels()).applyAll();
    // Fallback for missing signal declarations and wires.
    for (Entry<IOReference, Source> entry : namedSources.entries()) {
      IOReference ref = entry.getKey();
      Source source = entry.getValue();
      if (source.label().isPresent()) {
        LOGGER.warn("Dangling source: {} with label: {}", ref, source.label().get());
      } else {
        LOGGER.info("Unconnected source: {}", ref);
      }
    }
    for (Entry<IOReference, Sink> entry : danglingSinks.entries()) {
      LOGGER.error("Dangling sink: {}", entry.getKey());
    }
    currentVi.cleanUpDiagram();
    currentVi = null;
    namedSources = null;
    danglingSinks = null;
    concurrentStatementFallbacked = null;
  }

  @Override
  public void visit(component_instantiation_statement n) {
    concurrentStatementFallback = false;
    final EntityDeclaration entity = resolveEntity(new EntityName(n.instantiated_unit));
    String label = n.instantiation_label.label.representation();
    LOGGER.debug("Instance of: {} labelled: {}", entity.name(), label);
    final UniversalSubVI instance = new UniversalSubVI(currentVi, project, entity, of(label));
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
        portTerminal = instance.terminal(terminal.connectorIndex());
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
        portTerminal = instance.terminal(terminal.connectorIndex());
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
          new SourceEmitter(currentVi, danglingSinks, namedSources).emitWithSink(n, portTerminal);
        } else {
          new SinkEmitter(currentVi, danglingSinks, namedSources).emitWithSource(portTerminal, n);
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
    concurrentStatementFallback = false;
    MissingFeature.missing("Blocks are not supported at this time.", n);
  }

  @Override
  public void visit(process_statement n) {
    concurrentStatementFallback = false;
    Loop loop = new WhileLoop(currentVi, Optional.<String>absent());
    // Emit process body AND declarations.
    Formula formula =
        new FormulaNode(loop.diagram(), n.representation(), PROCESS_STATEMENT_PART_LABEL);
    // Connect wires.
    new ProcessSignalsEmitter(loop, formula, danglingSinks, namedSources).visit(n);
  }

  @Override
  public void visit(concurrent_procedure_call_statement n) {
    // TODO
  }

  @Override
  public void visit(concurrent_assertion_statement n) {
    concurrentStatementFallback = false;
    final Formula formula =
        new FormulaNode(currentVi, n.representation(), Optional.<String>absent());
    final SourceEmitter sourceEmitter = new SourceEmitter(currentVi, danglingSinks, namedSources);
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
    concurrentStatementFallback = false;
    final Formula formula =
        new FormulaNode(currentVi, n.representation(), Optional.<String>absent());
    final SinkEmitter sinkEmitter = new SinkEmitter(currentVi, danglingSinks, namedSources);
    final SourceEmitter sourceEmitter = new SourceEmitter(currentVi, danglingSinks, namedSources);
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
    // TODO
  }

  @Override
  public void visit(concurrent_statement n) {
    concurrentStatementFallback = true;
    super.visit(n);
    if (concurrentStatementFallback) {
      concurrentStatementFallbacked.append(n.representation()).append(System.lineSeparator());
    }
  }
}
