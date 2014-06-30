package stupaq.vhdl2lv;

import com.google.common.base.Verify;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import stupaq.MissingFeature;
import stupaq.labview.UID;
import stupaq.labview.scripting.EditableVI;
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
  private EditableVI currentVi;
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
    LOGGER.debug("Architecture of: " + entity.name());
    namedSources = new IOSources();
    danglingSinks = new IOSinks();
    currentVi = project.create(entity.name(), true);
    UID entityUid = currentVi.formulaNodeCreate(representation(entity.node()), "", false);
    for (ConstantDeclaration constant : entity.generics()) {
      UID terminal = currentVi.formulaNodeAddIO(entityUid, false, constant.reference().toString(),
          false);
      namedSources.put(constant.reference(), terminal);
    }
    for (PortDeclaration port : entity.ports()) {
      // IN and OUT are source and sink when we look from the outside (entity declaration).
      UID terminal = currentVi.formulaNodeAddIO(entityUid, port.direction() == PortDirection.OUT,
          port.reference().toString(), false);
      if (port.direction() == PortDirection.OUT) {
        danglingSinks.put(port.reference(), terminal);
      } else {
        namedSources.put(port.reference(), terminal);
      }
    }
    n.architecture_statement_part.accept(this);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Named sources:");
      for (Entry<IOReference, UID> entry : namedSources.entrySet()) {
        LOGGER.debug("\t" + entry.toString());
      }
      LOGGER.debug("Dangling sinks:");
      for (Entry<IOReference, UID> entry : danglingSinks.entries()) {
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
        UID source = namedSources.remove(ref);
        if (source != null) {
          for (UID sink : danglingSinks.removeAll(ref)) {
            LOGGER.debug("\t" + source + " => " + sink);
            currentVi.connectWire(source, sink, label);
          }
        }
      }
    });
    for (Entry<IOReference, UID> source : namedSources.entrySet()) {
      LOGGER.debug("Signal: " + source.getKey() + " connects:");
      for (UID sink : danglingSinks.removeAll(source.getKey())) {
        LOGGER.debug("\t" + source.getValue() + " => " + sink);
        currentVi.connectWire(source.getValue(), sink, "");
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
    final UID instanceUid = currentVi.formulaNodeCreate(entity.name(), label, false);
    sequence(n.nodeOptional, n.nodeOptional1).accept(new DepthFirstVisitor() {
      /** Context of {@link #visit(generic_map_aspect)}. */
      List<ConstantDeclaration> generics;
      /** Context of {@link #visit(port_map_aspect)}. */
      List<PortDeclaration> ports;
      /** Context of {@link #visit(association_list)}. */
      int index;
      /** Context of {@link #visit(association_element)}. */
      boolean portIsSink;
      /** Context of {@link #visit(association_element)}. */
      UID portTerminal;

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
        index = 0;
        super.visit(n);
      }

      @Override
      public void visit(association_element n) {
        MissingFeature.throwIf(n.nodeOptional.present(),
            "Resolving associations is not yet implemented.");
        // Resolve port contract.
        String label;
        if (ports != null) {
          PortDeclaration port = ports.get(index++);
          portIsSink = port.direction() == PortDirection.IN;
          label = port.reference().toString();
        } else if (generics != null) {
          ConstantDeclaration constant = generics.get(index++);
          portIsSink = true;
          label = constant.reference().toString();
        } else {
          throw new AssertionError();
        }
        // Create port.
        portTerminal = currentVi.formulaNodeAddIO(instanceUid, portIsSink, label, false);
        // Resolve port assignment (rhs).
        n.actual_part.accept(this);
        // If everything went OK, the port context should be zeroed.
        Verify.verify(portTerminal == null, "Unresolved assignment in entity instantiation");
      }

      @Override
      public void visit(generic_map_aspect n) {
        generics = entity.generics();
        super.visit(n);
        generics = null;
      }

      @Override
      public void visit(port_map_aspect n) {
        ports = entity.ports();
        super.visit(n);
        ports = null;
      }

      @Override
      public void visit(expression n) {
        UID expressionUid = currentVi.formulaNodeCreate(representation(n), "", false);
        UID expressionTerminal = currentVi.formulaNodeAddIO(expressionUid, !portIsSink, "<result>",
            false);
        if (portIsSink) {
          currentVi.connectWire(expressionTerminal, portTerminal, "");
        } else {
          currentVi.connectWire(portTerminal, expressionTerminal, "");
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
