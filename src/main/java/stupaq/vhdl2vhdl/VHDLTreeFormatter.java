package stupaq.vhdl2vhdl;

import stupaq.vhdl93.ast.*;
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
  public void visit(concurrent_signal_assignment_statement n) {
    if (n.nodeOptional.present()) {
      n.nodeOptional.accept(this);
    }
    if (n.nodeOptional1.present()) {
      n.nodeOptional1.accept(this);
    }
    n.nodeChoice.accept(this);
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
  public void visit(design_unit n) {
    n.context_clause.accept(this);
    n.library_unit.accept(this);
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
  public void visit(interface_constant_declaration n) {
    if (n.nodeOptional.present()) {
      n.nodeOptional.accept(this);
    }
    n.identifier_list.accept(this);
    n.nodeToken.accept(this);
    if (n.nodeOptional1.present()) {
      n.nodeOptional1.accept(this);
    }
    n.subtype_indication.accept(this);
    if (n.nodeOptional2.present()) {
      n.nodeOptional2.accept(this);
    }
  }

  @Override
  public void visit(interface_element n) {
    add(force());
    n.interface_declaration.accept(this);
  }

  @Override
  public void visit(interface_signal_declaration n) {
    if (n.nodeOptional.present()) {
      n.nodeOptional.accept(this);
    }
    n.identifier_list.accept(this);
    n.nodeToken.accept(this);
    if (n.nodeOptional1.present()) {
      n.nodeOptional1.accept(this);
    }
    n.subtype_indication.accept(this);
    if (n.nodeOptional2.present()) {
      n.nodeOptional2.accept(this);
    }
    if (n.nodeOptional3.present()) {
      n.nodeOptional3.accept(this);
    }
  }

  @Override
  public void visit(library_clause n) {
    n.nodeToken.accept(this);
    n.logical_name_list.accept(this);
    n.nodeToken1.accept(this);
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
  public void visit(range_attribute_value n) {
    n.simple_expression.accept(this);
    n.direction.accept(this);
    n.simple_expression1.accept(this);
  }

  @Override
  public void visit(signal_declaration n) {
    n.nodeToken.accept(this);
    n.identifier_list.accept(this);
    n.nodeToken1.accept(this);
    n.subtype_indication.accept(this);
    if (n.nodeOptional.present()) {
      n.nodeOptional.accept(this);
    }
    if (n.nodeOptional1.present()) {
      n.nodeOptional1.accept(this);
    }
    n.nodeToken2.accept(this);
  }

  @Override
  public void visit(subprogram_declarative_part n) {
    processOptionalList(null, n.nodeListOptional, force());
  }

  @Override
  public void visit(use_clause n) {
    n.nodeToken.accept(this);
    n.selected_name.accept(this);
    processOptionalList(null, n.nodeListOptional, space());
    n.nodeToken1.accept(this);
  }
}
