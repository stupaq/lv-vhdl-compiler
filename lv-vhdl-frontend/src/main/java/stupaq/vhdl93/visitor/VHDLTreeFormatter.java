package stupaq.vhdl93.visitor;

import stupaq.vhdl93.ast.architecture_declaration;
import stupaq.vhdl93.ast.component_instantiation_statement;
import stupaq.vhdl93.ast.conditional_signal_assignment;
import stupaq.vhdl93.ast.context_clause;
import stupaq.vhdl93.ast.design_file;
import stupaq.vhdl93.ast.entity_declaration;
import stupaq.vhdl93.ast.entity_declarative_part;
import stupaq.vhdl93.ast.entity_header;
import stupaq.vhdl93.ast.port_clause;
import stupaq.vhdl93.ast.process_statement;
import stupaq.vhdl93.ast.selected_signal_assignment;

public class VHDLTreeFormatter extends LineBreakingTreeFormatter {
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
  public void visit(context_clause n) {
    if (n.nodeListOptional.present()) {
      n.nodeListOptional.accept(this);
      add(force());
    }
  }

  @Override
  public void visit(entity_declaration n) {
    n.nodeToken.accept(this);
    n.entity_identifier.identifier.accept(this);
    n.nodeToken1.accept(this);
    add(indent());
    add(force());
    n.entity_header.accept(this);
    n.entity_declarative_part.accept(this);
    add(outdent());
    if (n.nodeOptional.present()) {
      n.nodeOptional.accept(this);
    }
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
  public void visit(entity_declarative_part n) {
    processOptionalList(null, n.nodeListOptional, force());
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
    add(outdent());
    n.nodeToken3.accept(this);
    add(indent());
    add(force());
    n.architecture_statement_part.accept(this);
    add(outdent());
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
  public void visit(conditional_signal_assignment n) {
    n.target.accept(this);
    add(space());
    n.nodeToken.accept(this);
    add(space());
    n.options_.accept(this);
    n.conditional_waveforms.accept(this);
    n.nodeToken1.accept(this);
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
  public void visit(selected_signal_assignment n) {
    n.nodeToken.accept(this);
    n.expression.accept(this);
    n.nodeToken1.accept(this);
    n.target.accept(this);
    add(space());
    n.nodeToken2.accept(this);
    add(space());
    n.options_.accept(this);
    n.selected_waveforms.accept(this);
    n.nodeToken3.accept(this);
  }

  @Override
  public void visit(process_statement n) {
    if (n.nodeOptional.present()) {
      n.nodeOptional.accept(this);
    }
    if (n.nodeOptional1.present()) {
      n.nodeOptional1.accept(this);
    }
    n.nodeToken.accept(this);
    if (n.nodeOptional2.present()) {
      n.nodeOptional2.accept(this);
    }
    if (n.nodeOptional3.present()) {
      add(space());
      n.nodeOptional3.accept(this);
    }
    add(indent());
    add(force());
    n.process_declarative_part.accept(this);
    add(outdent());
    n.nodeToken1.accept(this);
    add(indent());
    add(force());
    n.process_statement_part.accept(this);
    add(outdent());
    n.nodeToken2.accept(this);
    if (n.nodeOptional4.present()) {
      n.nodeOptional4.accept(this);
    }
    n.nodeToken3.accept(this);
    if (n.nodeOptional5.present()) {
      n.nodeOptional5.accept(this);
    }
    n.nodeToken4.accept(this);
  }
}
