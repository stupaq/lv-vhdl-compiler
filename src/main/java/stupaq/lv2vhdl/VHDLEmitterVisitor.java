package stupaq.lv2vhdl;

import java.io.PrintWriter;

import stupaq.ast.NoOpVisitor;
import stupaq.vhdl93.*;

public class VHDLEmitterVisitor extends NoOpVisitor<Void, Void, RuntimeException>
    implements VHDL93ParserConstants {
  private int indentLevel;
  private String indent;
  private PrintWriter out;
  private SimpleNode node;
  private int child;

  public VHDLEmitterVisitor(PrintWriter out) {
    this.out = out;
  }

  private void start(SimpleNode node) {
    this.node = node;
    child = 0;
  }

  private void next() {
    next(0);
  }

  private void next(int diff) {
    if (child < node.jjtGetNumChildren()) {
      indentLevel += diff;
      node.jjtGetChild(child).jjtAccept(this, null);
      indentLevel -= diff;
      child++;
    }
  }

  private void println() {
    out.println();
    for (int i = 0; i < indentLevel; i++) {
      out.print(indent);
    }
  }

  private void println(Object o) {
    out.print(o);
    println();
  }

  private void print(Object o) {
    out.print(o);
  }

  private void token(int token) {
    String image = tokenImage[token];
    out.print(image.substring(1, image.length() - 1) + " ");
  }

  public void flush() {
    out.flush();
  }

  @Override
  public Void visit(SimpleNode node, Void data) {
    node.childrenAccept(this, null);
    return null;
  }

  /** AST node-specific printers. */

  @Override
  public Void visit(ASTactual_part node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTaggregate node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTalias_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTalias_designator node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTallocator node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTarchitecture_body node, Void data) {
    start(node);
    token(ARCHITECTURE);
    next();
    token(OF);
    next();
    token(IS);
    next(1);
    token(BEGIN);
    next(1);
    token(END);
    next();
    next();
    println(";");
    return null;
  }

  @Override
  public Void visit(ASTarchitecture_declarative_part node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTarchitecture_statement_part node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTassertion node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTassertion_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTassociation_element node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTassociation_list node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTattribute_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTattribute_name node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTattribute_specification node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTbinding_indication node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTblock_configuration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTblock_declarative_part node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTblock_header node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTblock_specification node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTblock_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTblock_statement_part node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTcase_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTcase_statement_alternative node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTchoice node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTchoices node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTcomponent_configuration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTcomponent_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTcomponent_instantiation_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTcomponent_specification node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTcomposite_type_definition node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTconcurrent_assertion_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTconcurrent_procedure_call_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTconcurrent_signal_assignment_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTconditional_signal_assignment node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTconditional_waveforms node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTconfiguration_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTconfiguration_declarative_part node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTconfiguration_specification node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTconstant_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTconstrained_array_definition node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTcontext_clause node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTdelay_mechanism node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTdesign_file node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTdesign_unit node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTdirection node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTdisconnection_specification node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTelement_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTentity_aspect node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTentity_class_entry node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTentity_class_entry_list node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTentity_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTentity_declarative_part node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTentity_designator node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTentity_header node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTentity_name_list node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTentity_specification node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTentity_statement_part node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTentity_tag node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTenumeration_literal node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTenumeration_type_definition node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTexit_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTrelation node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTfactor node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTfile_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTfile_open_information node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTformal_parameter_list node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTfull_type_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTfunction_call node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTgenerate_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTgeneration_scheme node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTgeneric_clause node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTgroup_constituent node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTgroup_constituent_list node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTgroup_template_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTgroup_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTguarded_signal_specification node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTidentifier node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTidentifier_list node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTif_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTincomplete_type_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTindex_constraint node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTindex_specification node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTindex_subtype_definition node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTindexed_name node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTinstantiated_unit node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTinstantiation_list node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTinterface_constant_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTinterface_file_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTinterface_list node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTinterface_signal_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTinterface_variable_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTiteration_scheme node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTliteral node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTlogical_name_list node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTloop_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTmode node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTname node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTnext_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTnull_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASToperator_symbol node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASToptions_ node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTpackage_body node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTpackage_body_declarative_part node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTpackage_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTpackage_declarative_part node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTparameter_specification node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTphysical_literal node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTphysical_type_definition node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTport_clause node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTport_map_aspect node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTprefix node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTprocedure_call node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTprocedure_call_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTprocess_declarative_part node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTprocess_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTprocess_statement_part node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTqualified_expression node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTrange node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTrecord_type_definition node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTshift_expression node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTreport_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTreturn_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTsecondary_unit_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTselected_name node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTselected_signal_assignment node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTselected_waveforms node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTsensitivity_list node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTsequence_of_statements node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTsimple_expression node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTsign node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTsignal_assignment_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTsignal_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTsignal_kind node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTsignal_list node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTsignature node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTterm node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTslice_name node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTsubprogram_body node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTsubprogram_declarative_part node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTsubprogram_specification node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTsubprogram_statement_part node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTsubtype_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTsubtype_indication node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTtype_conversion node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTunconstrained_array_definition node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTuse_clause node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTvariable_assignment_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTvariable_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTwait_statement node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTwaveform node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTwaveform_element node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTshared_variable_declaration node, Void data) {
    return super.visit(node, data);
  }

  @Override
  public Void visit(ASTerror_skipto node, Void data) {
    return super.visit(node, data);
  }
}
