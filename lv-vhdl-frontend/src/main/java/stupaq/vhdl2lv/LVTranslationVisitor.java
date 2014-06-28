package stupaq.vhdl2lv;

import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Map.Entry;

import stupaq.labview.UID;
import stupaq.labview.scripting.EditableVI;
import stupaq.vhdl93.ast.architecture_declaration;
import stupaq.vhdl93.ast.component_instantiation_statement;
import stupaq.vhdl93.ast.design_file;
import stupaq.vhdl93.ast.entity_declaration;
import stupaq.vhdl93.ast.instantiated_unit;
import stupaq.vhdl93.visitor.DepthFirstVisitor;
import stupaq.vhdl93.visitor.FlattenNestedListsVisitor;

import static stupaq.vhdl93.ast.ASTGetters.representation;

public class LVTranslationVisitor extends DepthFirstVisitor {
  private static final Logger LOGGER = LoggerFactory.getLogger(LVTranslationVisitor.class);
  /** Context of {@link design_file}. */
  private final Map<String, EntityDeclaration> knownEntities = Maps.newHashMap();
  /** Context of {@link design_file}. */
  private final LVProject project;
  /** Context of {@link architecture_declaration}. */
  private EditableVI currentVi;
  private String statementLabel;

  public LVTranslationVisitor(LVProject project) {
    this.project = project;
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
    String architectureId = representation(n.architecture_identifier.identifier);
    EntityDeclaration entity = knownEntities.get(representation(n.entity_name));
    currentVi = project.create(architectureId, true);
    UID entityUID = currentVi.inlineCNodeCreate(representation(entity.node()));
    n.architecture_statement_part.accept(this);
    // FIXME
    n.architecture_declarative_part.accept(this);
  }

  @Override
  public void visit(component_instantiation_statement n) {
    statementLabel = representation(n.instantiation_label);
    super.visit(n);
  }

  @Override
  public void visit(instantiated_unit n) {
    super.visit(n);
  }
}
