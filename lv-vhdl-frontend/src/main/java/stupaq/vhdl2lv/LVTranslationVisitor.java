package stupaq.vhdl2lv;

import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Map.Entry;

import stupaq.labview.scripting.EditableVI;
import stupaq.vhdl93.ast.architecture_body;
import stupaq.vhdl93.ast.design_file;
import stupaq.vhdl93.ast.entity_declaration;
import stupaq.vhdl93.visitor.DepthFirstVisitor;
import stupaq.vhdl93.visitor.FlattenNestedListsVisitor;

import static stupaq.vhdl93.ast.PropertyAccessor.representation;

public class LVTranslationVisitor extends DepthFirstVisitor {
  private static final Logger LOGGER = LoggerFactory.getLogger(LVTranslationVisitor.class);
  private final Map<String, EntityDeclaration> knownEntities = Maps.newHashMap();
  private final LVProject project;
  private EditableVI currentVi;

  public LVTranslationVisitor(LVProject project) {
    this.project = project;
  }

  @Override
  public void visit(architecture_body n) {
    String name = representation(n.identifier);
    project.remove(name);
    currentVi = project.create(name);
    n.architecture_statement_part.accept(this);
    // FIXME
    n.architecture_declarative_part.accept(this);
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
    knownEntities.put(entity.identifier(), entity);
  }
}
