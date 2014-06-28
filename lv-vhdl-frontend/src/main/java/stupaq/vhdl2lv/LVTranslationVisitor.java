package stupaq.vhdl2lv;

import com.google.common.base.Verify;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Map.Entry;

import stupaq.labview.UID;
import stupaq.labview.scripting.EditableVI;
import stupaq.vhdl93.ast.NodeSequence;
import stupaq.vhdl93.ast.SimpleNode;
import stupaq.vhdl93.ast.architecture_declaration;
import stupaq.vhdl93.ast.component_instantiation_statement;
import stupaq.vhdl93.ast.design_file;
import stupaq.vhdl93.ast.entity_declaration;
import stupaq.vhdl93.visitor.DepthFirstVisitor;
import stupaq.vhdl93.visitor.FlattenNestedListsVisitor;

import static stupaq.vhdl93.ast.ASTGetters.name;
import static stupaq.vhdl93.ast.ASTGetters.representation;

public class LVTranslationVisitor extends DepthFirstVisitor {
  private static final Logger LOGGER = LoggerFactory.getLogger(LVTranslationVisitor.class);
  /** Context of {@link design_file}. */
  private final Map<String, EntityDeclaration> knownEntities = Maps.newHashMap();
  /** Context of {@link design_file}. */
  private final LVProject project;
  /** Context of {@link architecture_declaration}. */
  private EditableVI currentVi;

  public LVTranslationVisitor(LVProject project) {
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

  private static NodeSequence sequence(SimpleNode... args) {
    NodeSequence node = new NodeSequence(args.length);
    for (SimpleNode n : args) {
      node.addNode(n);
    }
    return node;
  }

  @Override
  public void visit(design_file n) {
    n.accept(new FlattenNestedListsVisitor());
    super.visit(n);
    if (LOGGER.isDebugEnabled()) {
      for (Entry entry : knownEntities.entrySet()) {
        LOGGER.debug(entry.toString());
      }
    }
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
    currentVi.cleanUpDiagram();
  }

  @Override
  public void visit(component_instantiation_statement n) {
    EntityDeclaration entity = resolveEntity(name(n.instantiated_unit));
    String label = representation(n.instantiation_label.label);
    UID instanceUid = currentVi.inlineCNodeCreate(representation(
        sequence(n.instantiated_unit, n.nodeOptional, n.nodeOptional1, n.nodeToken1)), label);
    // FIXME
  }
}
