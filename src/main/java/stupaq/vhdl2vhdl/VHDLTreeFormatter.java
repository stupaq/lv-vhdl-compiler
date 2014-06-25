package stupaq.vhdl2vhdl;

import stupaq.vhdl93.ast.architecture_body;
import stupaq.vhdl93.ast.architecture_declarative_part;
import stupaq.vhdl93.ast.architecture_statement_part;
import stupaq.vhdl93.ast.block_declarative_part;
import stupaq.vhdl93.ast.component_instantiation_statement;
import stupaq.vhdl93.ast.configuration_declarative_part;
import stupaq.vhdl93.ast.context_clause;
import stupaq.vhdl93.ast.design_file;
import stupaq.vhdl93.ast.entity_declaration;
import stupaq.vhdl93.ast.entity_declarative_part;
import stupaq.vhdl93.ast.entity_header;
import stupaq.vhdl93.ast.entity_statement_part;
import stupaq.vhdl93.ast.interface_element;
import stupaq.vhdl93.ast.package_body_declarative_part;
import stupaq.vhdl93.ast.package_declarative_part;
import stupaq.vhdl93.ast.port_clause;
import stupaq.vhdl93.ast.subprogram_declarative_part;
import stupaq.vhdl93.visitor.UserDefinedTreeFormatter;

public class VHDLTreeFormatter extends UserDefinedTreeFormatter {
  public VHDLTreeFormatter() {
    this(4, 80);
  }

  public VHDLTreeFormatter(int indentAmt, int wrapWidth) {
    super(indentAmt, wrapWidth, true);
  }

  @Override
  public void visit(architecture_body n) {
    n.nodeToken.accept(this);
    n.identifier.accept(this);
    n.nodeToken1.accept(this);
    n.entity_name.accept(this);
    n.nodeToken2.accept(this);
    add(indent());
    add(force());
    n.architecture_declarative_part.accept(this);
    add(outdent());
    add(force());
    n.nodeToken3.accept(this);
    add(indent());
    add(force());
    n.architecture_statement_part.accept(this);
    add(outdent());
    add(force());
    n.nodeToken4.accept(this);
    if (n.nodeOptional.present()) {
      n.nodeOptional.accept(this);
    }
    if (n.nodeOptional1.present()) {
      n.nodeOptional1.accept(this);
    }
    n.nodeToken5.accept(this);
  }

  @Override
  public void visit(architecture_declarative_part n) {
    processOptionalList(null, n.nodeListOptional, force());
  }

  @Override
  public void visit(architecture_statement_part n) {
    processOptionalList(null, n.nodeListOptional, force());
  }

  @Override
  public void visit(block_declarative_part n) {
    processOptionalList(null, n.nodeListOptional, force());
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
  public void visit(configuration_declarative_part n) {
    processOptionalList(null, n.nodeListOptional, force());
  }

  @Override
  public void visit(context_clause n) {
    processOptionalList(null, n.nodeListOptional, force());
    add(force());
  }

  @Override
  public void visit(design_file n) {
    add(force());
    processList(n.nodeList, force());
    n.nodeToken.accept(this);
  }

  @Override
  public void visit(entity_declaration n) {
    n.nodeToken.accept(this);
    n.identifier.accept(this);
    n.nodeToken1.accept(this);
    add(indent());
    add(force());
    n.entity_header.accept(this);
    n.entity_declarative_part.accept(this);
    add(outdent());
    if (n.nodeOptional.present()) {
      n.nodeOptional.accept(this);
    }
    add(force());
    n.nodeToken2.accept(this);
    if (n.nodeOptional1.present()) {
      n.nodeOptional1.accept(this);
    }
    if (n.nodeOptional2.present()) {
      n.nodeOptional2.accept(this);
    }
    n.nodeToken3.accept(this);
  }

  @Override
  public void visit(entity_declarative_part n) {
    processOptionalList(null, n.nodeListOptional, force());
  }

  @Override
  public void visit(entity_header n) {
    if (n.nodeOptional.present()) {
      n.nodeOptional.accept(this);
      add(force());
    }
    if (n.nodeOptional1.present()) {
      n.nodeOptional1.accept(this);
    }
  }

  @Override
  public void visit(entity_statement_part n) {
    processOptionalList(null, n.nodeListOptional, force());
  }

  @Override
  public void visit(interface_element n) {
    add(force());
    n.interface_declaration.accept(this);
  }

  @Override
  public void visit(package_body_declarative_part n) {
    processOptionalList(null, n.nodeListOptional, force());
  }

  @Override
  public void visit(package_declarative_part n) {
    processOptionalList(null, n.nodeListOptional, force());
  }

  @Override
  public void visit(port_clause n) {
    n.nodeToken.accept(this);
    n.nodeToken1.accept(this);
    add(indent());
    n.port_list.accept(this);
    add(outdent());
    add(force());
    n.nodeToken2.accept(this);
    n.nodeToken3.accept(this);
  }

  @Override
  public void visit(subprogram_declarative_part n) {
    processOptionalList(null, n.nodeListOptional, force());
  }
}
