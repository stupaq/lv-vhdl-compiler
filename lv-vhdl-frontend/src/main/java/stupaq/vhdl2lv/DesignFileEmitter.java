package stupaq.vhdl2lv;

import com.google.common.base.Optional;
import com.google.common.base.Verify;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Map.Entry;

import stupaq.MissingFeature;
import stupaq.concepts.ConstantDeclaration;
import stupaq.concepts.EntityDeclaration;
import stupaq.concepts.EntityName;
import stupaq.concepts.IOReference;
import stupaq.concepts.Identifier;
import stupaq.concepts.PortDeclaration;
import stupaq.concepts.PortDeclaration.PortDirection;
import stupaq.labview.scripting.hierarchy.Control;
import stupaq.labview.scripting.hierarchy.Formula;
import stupaq.labview.scripting.hierarchy.FormulaNode;
import stupaq.labview.scripting.hierarchy.Indicator;
import stupaq.labview.scripting.hierarchy.SubVI;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.labview.scripting.hierarchy.VI;
import stupaq.labview.scripting.hierarchy.Wire;
import stupaq.vhdl2lv.WiringRules.FallbackLabels;
import stupaq.vhdl93.ast.*;
import stupaq.vhdl93.visitor.DepthFirstVisitor;
import stupaq.vhdl93.visitor.FlattenNestedListsVisitor;
import stupaq.vhdl93.visitor.GJNoArguDepthFirst;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Optional.of;
import static stupaq.labview.scripting.tools.ControlStyle.NUMERIC_DBL;
import static stupaq.labview.scripting.tools.ControlStyle.NUMERIC_I32;
import static stupaq.vhdl93.ast.ASTBuilders.sequence;
import static stupaq.vhdl93.ast.ASTGetters.representation;

class DesignFileEmitter extends DepthFirstVisitor {
  private static final Logger LOGGER = LoggerFactory.getLogger(DesignFileEmitter.class);
  /** Context of {@link #visit(design_file)}. */
  private final Map<EntityName, EntityDeclaration> knownEntities = Maps.newHashMap();
  /** Context of {@link #visit(design_file)}. */
  private final LVProject project;
  /** Context of {@link #visit(architecture_declaration)}. */
  private VI currentVi;
  /** Context of {@link #visit(architecture_declaration)}. */
  private IOSources namedSources;
  /** Context of {@link #visit(architecture_declaration)}. */
  private IOSinks danglingSinks;

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
    EntityDeclaration entity = resolveEntity(new EntityName(n.entity_name));
    Identifier architecture = new Identifier(n.architecture_identifier.identifier);
    LOGGER.debug("Architecture: {} of: {}", architecture, entity.name());
    currentVi = project.create(entity.name(), true);
    namedSources = new IOSources();
    danglingSinks = new IOSinks();
    int connPanelIndex = 0;
    for (ConstantDeclaration constant : entity.generics()) {
      Optional<String> label = of(constant.reference().toString());
      Terminal terminal =
          new Control(currentVi, NUMERIC_I32, label, connPanelIndex++).endpoint().get();
      namedSources.put(constant.reference(), terminal);
    }
    for (PortDeclaration port : entity.ports()) {
      // IN and OUT are source and sink when we look from the outside (entity declaration).
      Terminal terminal;
      Optional<String> label = of(port.reference().toString());
      if (port.direction() == PortDirection.OUT) {
        terminal = new Indicator(currentVi, NUMERIC_DBL, label, connPanelIndex++).endpoint().get();
      } else {
        terminal = new Control(currentVi, NUMERIC_DBL, label, connPanelIndex++).endpoint().get();
      }
      if (port.direction() == PortDirection.OUT) {
        danglingSinks.put(port.reference(), terminal);
      } else {
        namedSources.put(port.reference(), terminal);
      }
    }
    n.architecture_statement_part.accept(this);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Named sources:");
      for (Entry<IOReference, Endpoint> entry : namedSources.entries()) {
        LOGGER.debug("\t{}", entry);
      }
      LOGGER.debug("Dangling sinks:");
      for (Entry<IOReference, Endpoint> entry : danglingSinks.entries()) {
        LOGGER.debug("\t{}", entry);
      }
    }
    // There will be no more named terminals in this scope.
    final FallbackLabels labelling = new FallbackLabels();
    n.architecture_declarative_part.accept(new DepthFirstVisitor() {
      @Override
      public void visit(constant_declaration n) {
        IOReference ref = new IOReference(n.identifier_list.identifier);
        Verify.verify(n.nodeOptional.present(), "Missing value for constant: %s", ref);
        // There will be no more dangling sinks than we see right now, we can connect this
        // constant to every pending sink and forget about it.
        Terminal terminal = n.nodeOptional.accept(new GJNoArguDepthFirst<Terminal>() {
          @Override
          public Terminal visit(NodeSequence n) {
            return n.nodes.get(1).accept(this);
          }

          @Override
          public Terminal visit(expression n) {
            return new SourceEmitter(currentVi, danglingSinks).emitFormula(n);
          }
        });
        Verify.verify(!namedSources.containsKey(ref), "Constant: %s has other sources", ref);
        String label = representation(
            sequence(n.nodeToken, n.identifier_list, n.nodeToken1, n.subtype_indication));
        namedSources.put(ref, terminal, label);
      }

      @Override
      public void visit(signal_declaration n) {
        MissingFeature.throwIf(n.nodeOptional1.present(), "Signal default is not supported");
        IOReference ref = new IOReference(n.identifier_list.identifier);
        String label = representation(n);
        labelling.put(ref, label);
      }
    });
    // All references and labels are resolved now.
    new WiringRules(currentVi, namedSources, danglingSinks, labelling).applyAll();
    // Fallback for missing signal declarations and wires.
    if (!labelling.isEmpty()) {
      for (Entry<IOReference, String> entry : labelling.remainingLabels()) {
        LOGGER.warn("Unused label: {} for source: {}", entry.getValue(), entry.getKey());
        // TODO emit missing signals, so they won't get lost
      }
    }
    SourceEmitter sourceEmitter = new SourceEmitter(currentVi, danglingSinks);
    for (Entry<IOReference, Endpoint> entry : danglingSinks.entries()) {
      // If this sink has an associated node then we have to emmit it.
      Endpoint sink = entry.getValue();
      if (sink.node().isPresent()) {
        Terminal source = sourceEmitter.emitFormula(sink.node().get(), false);
        new Wire(currentVi, source, sink.terminal(), Optional.<String>absent());
      } else {
        LOGGER.error("Dangling sink: {}", entry.getKey());
      }
    }
    SinkEmitter sinkEmitter = new SinkEmitter(currentVi, danglingSinks, namedSources);
    for (Entry<IOReference, Endpoint> entry : namedSources.entries()) {
      // If this source has an associated node then we have to emmit it.
      Endpoint source = entry.getValue();
      if (source.node().isPresent()) {
        Terminal sink = sinkEmitter.emitFormula(source.node().get(), false);
        new Wire(currentVi, source.terminal(), sink, Optional.<String>absent());
      } else {
        LOGGER.warn("Unconnected source: {}", entry.getKey());
      }
    }
    currentVi.cleanUpDiagram();
    currentVi = null;
    namedSources = null;
    danglingSinks = null;
  }

  @Override
  public void visit(component_instantiation_statement n) {
    final EntityDeclaration entity = resolveEntity(new EntityName(n.instantiated_unit));
    String label = representation(n.instantiation_label.label);
    LOGGER.debug("Instance of: {} labelled: {}", entity.name(), label);
    final SubVI instance = new SubVI(currentVi, project.resolve(entity.name()), label);
    sequence(n.nodeOptional, n.nodeOptional1).accept(new DepthFirstVisitor() {
      /**
       * Context of {@link #visit(generic_map_aspect)} and {@link
       * #visit(port_map_aspect)}.
       */
      boolean isGenericAspect;
      /** Context of {@link #visit(positional_association_list)}. */
      int elementIndex;
      /** Context of {@link #visit(actual_part)}. */
      int listIndex;
      /** Context of {@link #visit(actual_part)}. */
      boolean portIsSink;
      /** Context of {@link #visit(actual_part)}. */
      Terminal portTerminal;
      /** Context of {@link #visit(actual_part)}. */
      Optional<SimpleNode> accessorCode;

      @Override
      public void visit(identifier n) {
        IOReference ref = new IOReference(n);
        LOGGER.debug("\tidentifier={}", ref);
        if (portIsSink) {
          danglingSinks.put(ref, portTerminal, accessorCode);
        } else {
          namedSources.put(ref, portTerminal, accessorCode);
        }
        portTerminal = null;
      }

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
        LOGGER.debug("Port assignment: {}", representation(n));
        IOReference ref = new IOReference(n.formal_part.identifier);
        listIndex = entity.listIndex().get(ref);
        n.actual_part.accept(this);
      }

      @Override
      public void visit(positional_association_element n) {
        LOGGER.debug("Port assignment: {}", representation(n));
        listIndex = elementIndex++;
        n.actual_part.accept(this);
      }

      @Override
      public void visit(actual_part n) {
        // This follows from the fact that we first emit all generics.
        int connPanelIndexBase = isGenericAspect ? 0 : entity.generics().size();
        portTerminal = instance.terminals().get(connPanelIndexBase + listIndex);
        portIsSink =
            isGenericAspect || entity.ports().get(listIndex).direction() == PortDirection.IN;
        LOGGER.debug("\tlistIndex={}, portIsSink={}, portTerminal", listIndex, portIsSink);
        accessorCode = Optional.absent();
        n.nodeChoice.accept(this);
      }

      @Override
      public void visit(actual_part_inline_identifier n) {
        accessorCode = Optional.absent();
        super.visit(n);
      }

      @Override
      public void visit(actual_part_inline_expression n) {
        accessorCode = Optional.<SimpleNode>of(n);
        super.visit(n);
      }

      @Override
      public void visit(constant_expression n) {
        // We do not descent into the RHS.
      }

      @Override
      public void visit(actual_part_open n) {
        // This way we do nothing for <OPEN> which is very appropriate.
        portTerminal = null;
      }

      @Override
      public void visit(expression n) {
        LOGGER.debug("\texpression={}", representation(n));
        Terminal source, sink;
        if (portIsSink) {
          source = new SourceEmitter(currentVi, danglingSinks).emitFormula(n);
          sink = portTerminal;
        } else {
          source = portTerminal;
          sink = new SinkEmitter(currentVi, danglingSinks, namedSources).emitFormula(n);
        }
        new Wire(currentVi, source, sink, Optional.<String>absent());
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
    // TODO
  }

  @Override
  public void visit(process_statement n) {
    // TODO
  }

  @Override
  public void visit(concurrent_procedure_call_statement n) {
    // TODO
  }

  @Override
  public void visit(concurrent_assertion_statement n) {
    // TODO
  }

  @Override
  public void visit(concurrent_signal_assignment_statement n) {
    String label = n.accept(new GJNoArguDepthFirst<String>() {
      @Override
      public String visit(NodeSequence n) {
        return n.nodes.get(0).accept(this);
      }

      @Override
      public String visit(label n) {
        return representation(n.identifier);
      }
    });
    final Formula formula = new FormulaNode(currentVi, representation(n), fromNullable(label));
    final SinkEmitter sinkEmitter = new SinkEmitter(currentVi, danglingSinks, namedSources);
    final SourceEmitter sourceEmitter = new SourceEmitter(currentVi, danglingSinks);
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
}
