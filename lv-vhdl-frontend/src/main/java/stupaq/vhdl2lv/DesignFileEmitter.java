package stupaq.vhdl2lv;

import com.google.common.base.Verify;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

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
import stupaq.vhdl93.ast.port_map_aspect;
import stupaq.vhdl93.ast.process_statement;
import stupaq.vhdl93.ast.signal_declaration;
import stupaq.vhdl93.visitor.DepthFirstVisitor;
import stupaq.vhdl93.visitor.FlattenNestedListsVisitor;

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
  private IOSources namedSources = new IOSources();
  /** Context of {@link #visit(architecture_declaration)}. */
  private IOSinks danglingSinks = new IOSinks();

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
    currentVi = project.create(entity.name(), true);
    UID entityUid = currentVi.inlineCNodeCreate(representation(entity.node()), "");
    n.architecture_statement_part.accept(this);
    // FIXME
    n.architecture_declarative_part.accept(new DepthFirstVisitor() {
      @Override
      public void visit(constant_declaration n) {
        // FIXME
        super.visit(n);
      }

      @Override
      public void visit(signal_declaration n) {
        // FIXME
        super.visit(n);
      }
    });
    for (PortDeclaration port : entity.ports()) {
      UID terminal = currentVi.inlineCNodeAddIO(entityUid, port.direction() == PortDirection.IN,
          port.reference().toString());
      // FIXME
    }
    currentVi.cleanUpDiagram();
  }

  @Override
  public void visit(component_instantiation_statement n) {
    final EntityDeclaration entity = resolveEntity(name(n.instantiated_unit));
    String label = representation(n.instantiation_label.label);
    final UID instanceUid = currentVi.inlineCNodeCreate(entity.name(), label);
    n.nodeOptional.accept(new DepthFirstVisitor() {
      /** Context of {@link generic_map_aspect}. */
      List<ConstantDeclaration> generics;
      /** Context of {@link port_map_aspect}. */
      List<PortDeclaration> ports;
      /** Context of {@link association_list}. */
      int index;
      /** Context of {@link #visit(association_element)}. */
      boolean portIsSink;
      /** Result of {@link #visit(expression)}. */
      UID expressionResult;

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
        IOReference ref;
        if (generics != null) {
          ConstantDeclaration constant = generics.get(index++);
          portIsSink = true;
          ref = constant.reference();
        } else if (ports != null) {
          PortDeclaration port = ports.get(index++);
          portIsSink = port.direction() == PortDirection.IN;
          ref = port.reference();
        } else {
          throw new AssertionError();
        }
        // Resolve port assignment.
        expressionResult = null;
        n.actual_part.accept(this);
        // Create port.
        UID terminal = currentVi.inlineCNodeAddIO(instanceUid, portIsSink, ref.toString());
        // Create a wire or add to dangling sinks.
        if (portIsSink) {
          if (expressionResult != null) {
            currentVi.connectWire(expressionResult, terminal, "");
          } else {
            danglingSinks.put(ref, terminal);
          }
        } else {
          if (expressionResult != null) {
            currentVi.connectWire(terminal, expressionResult, "");
          }
          namedSources.put(ref, terminal);
        }
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
        UID expressionUid = currentVi.inlineCNodeCreate(representation(n), "");
        expressionResult = currentVi.inlineCNodeAddIO(expressionUid, !portIsSink, "<result>");
        // Note that we do not visit recursively in current setting, so we are sure,
        // that this is the top-level expression context.
      }
    });
  }

  @Override
  public void visit(block_statement n) {
    // FIXME
  }

  @Override
  public void visit(process_statement n) {
    // FIXME
  }

  @Override
  public void visit(concurrent_procedure_call_statement n) {
    // FIXME
  }

  @Override
  public void visit(concurrent_assertion_statement n) {
    // FIXME
  }

  @Override
  public void visit(concurrent_signal_assignment_statement n) {
    // FIXME
  }

  @Override
  public void visit(generate_statement n) {
    // FIXME
  }
}
