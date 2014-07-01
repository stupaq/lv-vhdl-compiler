package stupaq.vhdl2lv;

import com.google.common.base.Verify;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import stupaq.MissingFeature;
import stupaq.labview.scripting.hierarchy.Control;
import stupaq.labview.scripting.hierarchy.FormulaNode;
import stupaq.labview.scripting.hierarchy.Indicator;
import stupaq.labview.scripting.hierarchy.SubVI;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.labview.scripting.hierarchy.VI;
import stupaq.labview.scripting.hierarchy.Wire;
import stupaq.labview.scripting.tools.ControlCreate;
import stupaq.vhdl2lv.PortDeclaration.PortDirection;
import stupaq.vhdl93.ast.architecture_declaration;
import stupaq.vhdl93.ast.association_element;
import stupaq.vhdl93.ast.association_list;
import stupaq.vhdl93.ast.block_statement;
import stupaq.vhdl93.ast.component_instantiation_statement;
import stupaq.vhdl93.ast.concurrent_assertion_statement;
import stupaq.vhdl93.ast.concurrent_procedure_call_statement;
import stupaq.vhdl93.ast.concurrent_signal_assignment_statement;
import stupaq.vhdl93.ast.constant_declaration;
import stupaq.vhdl93.ast.design_file;
import stupaq.vhdl93.ast.entity_declaration;
import stupaq.vhdl93.ast.expression;
import stupaq.vhdl93.ast.generate_statement;
import stupaq.vhdl93.ast.generic_map_aspect;
import stupaq.vhdl93.ast.identifier;
import stupaq.vhdl93.ast.port_map_aspect;
import stupaq.vhdl93.ast.process_statement;
import stupaq.vhdl93.ast.signal_declaration;
import stupaq.vhdl93.visitor.DepthFirstVisitor;
import stupaq.vhdl93.visitor.FlattenNestedListsVisitor;

import static stupaq.vhdl93.ast.ASTBuilders.sequence;
import static stupaq.vhdl93.ast.ASTGetters.name;
import static stupaq.vhdl93.ast.ASTGetters.representation;

public class DesignFileEmitter extends DepthFirstVisitor {
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
      public void visit(expression n) {
        // FIXME
      }

      @Override
      public void visit(constant_declaration n) {
        IOReference ref = new IOReference(n.identifier_list.identifier);
        Verify.verify(n.nodeOptional.present(), "Value is missing for constant: %s",
            ref.toString());
        Verify.verify(!namedSources.containsKey(ref),
            "Constant and data source have the same name: %s", ref.toString());
        // FIXME
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
      /** Context of {@link #visit(generic_map_aspect)}. */
      List<ConstantDeclaration> generics;
      /** Context of {@link #visit(port_map_aspect)}. */
      List<PortDeclaration> ports;
      /** Context of {@link #visit(association_list)}. */
      int elementIndex;
      /** Context of {@link #visit(association_list)}. */
      int connPanelIndexBase;
      /** Context of {@link #visit(association_element)}. */
      boolean portIsSink;
      /** Context of {@link #visit(association_element)}. */
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
      public void visit(association_list n) {
        elementIndex = 0;
        super.visit(n);
      }

      @Override
      public void visit(association_element n) {
        MissingFeature.throwIf(n.nodeOptional.present(),
            "Resolving associations is not yet implemented."); // TODO
        int listIndex = elementIndex;
        int connPanelIndex = connPanelIndexBase + listIndex;
        // Resolve port contract.
        if (ports != null) {
          PortDeclaration port = ports.get(listIndex);
          portIsSink = port.direction() == PortDirection.IN;
        } else if (generics != null) {
          portIsSink = true;
        } else {
          throw new AssertionError();
        }
        // Create port.
        portTerminal = instance.terminals().get(connPanelIndex);
        // Resolve port assignment (rhs).
        n.actual_part.accept(this);
        // If everything went OK, the port context should be zeroed.
        Verify.verify(portTerminal == null, "Unresolved assignment in entity instantiation");
        elementIndex++;
      }

      @Override
      public void visit(generic_map_aspect n) {
        generics = entity.generics();
        connPanelIndexBase = 0;
        super.visit(n);
        generics = null;
      }

      @Override
      public void visit(port_map_aspect n) {
        ports = entity.ports();
        connPanelIndexBase = entity.generics().size();
        super.visit(n);
        ports = null;
      }

      @Override
      public void visit(expression n) {
        FormulaNode expression = new FormulaNode(currentVi, representation(n), "");
        Terminal terminal = expression.addIO(!portIsSink, "<result>");
        if (portIsSink) {
          new Wire(terminal, portTerminal, "");
        } else {
          new Wire(portTerminal, terminal, "");
        }
        portTerminal = null;
        // Note that we do not visit recursively in current setting, so we are sure,
        // that this is the top-level expression context.
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
