package stupaq.vhdl93.formatting;

import stupaq.vhdl93.ast.architecture_declaration;
import stupaq.vhdl93.ast.case_statement;
import stupaq.vhdl93.ast.component_instantiation_statement;
import stupaq.vhdl93.ast.design_file;
import stupaq.vhdl93.ast.design_unit;
import stupaq.vhdl93.ast.entity_declaration;
import stupaq.vhdl93.ast.entity_declarative_part;
import stupaq.vhdl93.ast.generic_clause;
import stupaq.vhdl93.ast.port_clause;
import stupaq.vhdl93.ast.process_declarative_part;
import stupaq.vhdl93.ast.process_statement;

public class VHDLTreeFormatter extends SpecialTokenHandlingFormatter {
  private static final int INDENT_SPACES = 4, LINE_WIDTH = 80;

  public VHDLTreeFormatter() {
    super(INDENT_SPACES, LINE_WIDTH);
  }

  @Override
  public void visit(design_file n) {
    processList(n.nodeList, force());
    n.nodeToken.accept(this);
  }

  @Override
  public void visit(design_unit n) {
    n.context_clause.accept(this);
    n.library_unit.accept(this);
  }

  @Override
  public void visit(entity_declaration n) {
    n.nodeToken.accept(this);
    n.entity_identifier.accept(this);
    n.nodeToken1.accept(this);
    add(indent());
    add(force());
    n.entity_header.accept(this);
    n.entity_declarative_part.accept(this);
    n.nodeOptional.accept(this);
    n.nodeToken2.accept(this);
    n.nodeOptional1.accept(this);
    n.nodeOptional2.accept(this);
    n.nodeToken3.accept(this);
  }

  @Override
  public void visit(architecture_declaration n) {
    n.nodeToken.accept(this);
    n.architecture_identifier.accept(this);
    n.nodeToken1.accept(this);
    n.entity_name.accept(this);
    n.nodeToken2.accept(this);
    add(indent());
    add(force());
    n.architecture_declarative_part.accept(this);
    n.nodeToken3.accept(this);
    n.architecture_statement_part.accept(this);
    n.nodeToken4.accept(this);
    n.nodeOptional.accept(this);
    n.nodeOptional1.accept(this);
    n.nodeToken5.accept(this);
  }

  @Override
  public void visit(entity_declarative_part n) {
    processOptionalList(null, n.nodeListOptional, force());
  }

  @Override
  public void visit(process_statement n) {
    n.nodeOptional.accept(this);
    n.nodeOptional1.accept(this);
    n.nodeToken.accept(this);
    n.nodeOptional2.accept(this);
    n.nodeOptional3.accept(this);
    add(indent());
    add(force());
    n.process_declarative_part.accept(this);
    n.nodeToken1.accept(this);
    n.process_statement_part.accept(this);
    n.nodeToken2.accept(this);
    n.nodeOptional4.accept(this);
    n.nodeToken3.accept(this);
    n.nodeOptional5.accept(this);
    n.nodeToken4.accept(this);
  }

  @Override
  public void visit(process_declarative_part n) {
    processOptionalList(null, n.nodeListOptional, force());
  }

  @Override
  public void visit(case_statement n) {
    n.nodeOptional.accept(this);
    n.nodeToken.accept(this);
    n.expression.accept(this);
    n.nodeToken1.accept(this);
    add(indent());
    add(force());
    n.nodeList.accept(this);
    n.nodeToken2.accept(this);
    n.nodeToken3.accept(this);
    n.nodeOptional1.accept(this);
    n.nodeToken4.accept(this);
  }

  @Override
  public void visit(component_instantiation_statement n) {
    n.instantiation_label.accept(this);
    n.nodeToken.accept(this);
    n.instantiated_unit.accept(this);
    if (n.nodeOptional.present()) {
      add(indent());
      add(force());
      n.nodeOptional.accept(this);
      add(outdent());
    }
    if (n.nodeOptional1.present()) {
      add(indent());
      add(force());
      n.nodeOptional1.accept(this);
      add(outdent());
    }
    n.nodeToken1.accept(this);
  }

  @Override
  public void visit(port_clause n) {
    n.nodeToken.accept(this);
    n.nodeToken1.accept(this);
    add(indent());
    n.port_list.accept(this);
    add(outdent());
    n.nodeToken2.accept(this);
    n.nodeToken3.accept(this);
  }

  @Override
  public void visit(generic_clause n) {
    n.nodeToken.accept(this);
    n.nodeToken1.accept(this);
    add(indent());
    n.generic_list.accept(this);
    add(outdent());
    n.nodeToken2.accept(this);
    n.nodeToken3.accept(this);
  }
}
