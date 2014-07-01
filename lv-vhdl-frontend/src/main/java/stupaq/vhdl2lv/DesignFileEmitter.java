package stupaq.vhdl2lv;

import com.google.common.base.Verify;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Map.Entry;

import stupaq.MissingFeature;
import stupaq.concepts.ConstantDeclaration;
import stupaq.concepts.EntityDeclaration;
import stupaq.concepts.IOReference;
import stupaq.concepts.PortDeclaration;
import stupaq.concepts.PortDeclaration.PortDirection;
import stupaq.labview.scripting.hierarchy.Control;
import stupaq.labview.scripting.hierarchy.Indicator;
import stupaq.labview.scripting.hierarchy.SubVI;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.labview.scripting.hierarchy.VI;
import stupaq.labview.scripting.hierarchy.Wire;
import stupaq.labview.scripting.tools.ControlCreate;
import stupaq.vhdl93.ast.*;
import stupaq.vhdl93.visitor.DepthFirstVisitor;
import stupaq.vhdl93.visitor.FlattenNestedListsVisitor;

import static stupaq.vhdl93.ast.ASTBuilders.sequence;
import static stupaq.vhdl93.ast.ASTGetters.name;
import static stupaq.vhdl93.ast.ASTGetters.representation;

class DesignFileEmitter extends DepthFirstVisitor {
  private static final Logger LOGGER = LoggerFactory.getLogger(DesignFileEmitter.class);
  /** Context of {@link #visit(design_file)}. */
  private final Map<String, EntityDeclaration> knownEntities = Maps.newHashMap();
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

  private EntityDeclaration resolveEntity(String entityName) {
    EntityDeclaration entity = knownEntities.get(entityName);
    if (entity == null) {
      entity = knownEntities.get(EntityDeclaration.DEFAULT_LIBRARY_PREFIX + entityName);
    }
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
    EntityDeclaration entity = resolveEntity(representation(n.entity_name));
    String architecture = representation(n.architecture_identifier.identifier);
    LOGGER.debug("Architecture: " + architecture + " of: " + entity.name());
    namedSources = new IOSources();
    danglingSinks = new IOSinks();
    currentVi = project.create(entity.name(), true);
    int connPanelIndex = 0;
    for (ConstantDeclaration constant : entity.generics()) {
      Terminal terminal =
          new Control(currentVi, ControlCreate.NUMERIC, constant.reference().toString(),
              connPanelIndex++).terminal();
      namedSources.put(constant.reference(), terminal);
    }
    for (PortDeclaration port : entity.ports()) {
      // IN and OUT are source and sink when we look from the outside (entity declaration).
      Terminal terminal;
      if (port.direction() == PortDirection.OUT) {
        terminal = new Indicator(currentVi, ControlCreate.NUMERIC, port.reference().toString(),
            connPanelIndex++).terminal();
      } else {
        terminal = new Control(currentVi, ControlCreate.NUMERIC, port.reference().toString(),
            connPanelIndex++).terminal();
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
      for (Entry<IOReference, Terminal> entry : namedSources.entrySet()) {
        LOGGER.debug("\t" + entry.toString());
      }
      LOGGER.debug("Dangling sinks:");
      for (Entry<IOReference, Terminal> entry : danglingSinks.entries()) {
        LOGGER.debug("\t" + entry.toString());
      }
    }
    n.architecture_declarative_part.accept(new DepthFirstVisitor() {
      @Override
      public void visit(constant_declaration n) {
        IOReference ref = new IOReference(n.identifier_list.identifier);
        Verify.verify(n.nodeOptional.present(), "Value is missing for constant: %s",
            ref.toString());
        Verify.verify(!namedSources.containsKey(ref),
            "Constant and data source have the same name: %s", ref.toString());
        throw new MissingFeature("Constants are not implemented (yet).");
      }

      @Override
      public void visit(signal_declaration n) {
        MissingFeature.throwIf(n.nodeOptional1.present(), "Signal default is not supported");
        String label = representation(n);
        IOReference ref = new IOReference(n.identifier_list.identifier);
        LOGGER.debug("Signal: " + ref + " connects:");
        Terminal source = namedSources.remove(ref);
        if (source != null) {
          for (Terminal sink : danglingSinks.removeAll(ref)) {
            LOGGER.debug("\t" + source + " => " + sink);
            new Wire(source, sink, label);
          }
        }
      }
    });
    for (Entry<IOReference, Terminal> source : namedSources.entrySet()) {
      LOGGER.debug("Signal: " + source.getKey() + " connects:");
      for (Terminal sink : danglingSinks.removeAll(source.getKey())) {
        LOGGER.debug("\t" + source.getValue() + " => " + sink);
        new Wire(source.getValue(), sink, "");
      }
    }
    currentVi.cleanUpDiagram();
    namedSources = null;
    danglingSinks = null;
    currentVi = null;
  }

  @Override
  public void visit(component_instantiation_statement n) {
    final EntityDeclaration entity = resolveEntity(name(n.instantiated_unit));
    String label = representation(n.instantiation_label.label);
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

      @Override
      public void visit(identifier n) {
        IOReference ref = new IOReference(representation(n));
        if (portIsSink) {
          danglingSinks.put(ref, portTerminal);
        } else {
          namedSources.put(ref, portTerminal);
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
        IOReference ref = new IOReference(representation(n.formal_part.identifier));
        listIndex = entity.listIndex().get(ref);
        n.actual_part.accept(this);
      }

      @Override
      public void visit(positional_association_element n) {
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
        super.visit(n);
      }

      @Override
      public void visit(actual_part_open n) {
        // This way we do nothing for <OPEN> which is very appropriate.
        portTerminal = null;
      }

      @Override
      public void visit(expression n) {
        Terminal source, sink;
        if (portIsSink) {
          source = new ExpressionSourceEmitter(currentVi, danglingSinks).emit(n);
          sink = portTerminal;
        } else {
          source = portTerminal;
          sink = new ExpressionSinkEmitter(currentVi).emit(n);
        }
        new Wire(source, sink, "");
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
    // TODO
  }

  @Override
  public void visit(generate_statement n) {
    // TODO
  }

}
