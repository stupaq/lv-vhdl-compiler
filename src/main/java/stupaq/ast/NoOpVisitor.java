package stupaq.ast;

import stupaq.vhdl93.*;

public abstract class NoOpVisitor<Result, Argument, Failure extends Exception>
    implements VHDL93ParserVisitor<Result, Argument, Failure> {
  @Override
  public Result visit(SimpleNode node, Argument data) throws Failure {
    return null;
  }

  @Override
  public Result visit(ASTactual_part node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTaggregate node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTalias_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTalias_designator node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTallocator node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTarchitecture_body node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTarchitecture_declarative_part node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTarchitecture_statement_part node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTunconstrained_nature_definition node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTconstrained_nature_definition node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTassertion node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTassertion_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTassociation_element node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTassociation_list node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTattribute_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTattribute_name node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTattribute_specification node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTbinding_indication node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTblock_configuration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTblock_declarative_part node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTblock_header node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTblock_specification node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTblock_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTblock_statement_part node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTbreak_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTbreak_list node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTbreak_element node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTcase_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTcase_statement_alternative node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTchoice node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTchoices node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTcomponent_configuration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTcomponent_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTcomponent_instantiation_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTcomponent_specification node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTcomposite_type_definition node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTconcurrent_assertion_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTconcurrent_procedure_call_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTconcurrent_signal_assignment_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTconditional_signal_assignment node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTconditional_waveforms node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTconfiguration_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTconfiguration_declarative_part node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTconfiguration_specification node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTconstant_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTconstrained_array_definition node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTcontext_clause node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTterminal_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTterminal_aspect node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTdelay_mechanism node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTdesign_file node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTdesign_unit node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTdirection node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTdisconnection_specification node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTelement_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTentity_aspect node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTentity_class_entry node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTentity_class_entry_list node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTentity_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTnature_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsubnature_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsubnature_indication node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTentity_declarative_part node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTentity_designator node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTentity_header node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTentity_name_list node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTentity_specification node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTentity_statement_part node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTentity_tag node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTenumeration_literal node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTenumeration_type_definition node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTexit_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTrelation node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTfactor node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTfile_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTfile_open_information node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTformal_parameter_list node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTfull_type_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTfunction_call node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTgenerate_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTconcurrent_break_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTgeneration_scheme node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTgeneric_clause node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTgroup_constituent node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTgroup_constituent_list node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTgroup_template_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTgroup_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTguarded_signal_specification node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTidentifier node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTidentifier_list node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTif_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTincomplete_type_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTindex_constraint node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTindex_specification node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTindex_subtype_definition node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTindexed_name node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTinstantiated_unit node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTinstantiation_list node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTinterface_constant_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTinterface_terminal_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTinterface_quantity_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTinterface_file_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTinterface_list node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTinterface_signal_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTinterface_variable_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTiteration_scheme node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTliteral node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTlogical_name_list node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTloop_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTmode node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTname node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTnature_element_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTnext_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTnull_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASToperator_symbol node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASToptions_ node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTpackage_body node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTpackage_body_declarative_part node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTpackage_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTpackage_declarative_part node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTparameter_specification node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTphysical_literal node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTphysical_type_definition node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTport_clause node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTport_map_aspect node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTprefix node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTprocedure_call node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTprocedure_call_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTprocess_declarative_part node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTprocess_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTprocess_statement_part node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTqualified_expression node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTrange node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTrecord_nature_definition node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTrecord_type_definition node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTshift_expression node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTreport_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTreturn_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTscalar_nature_definition node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsecondary_unit_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTselected_name node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTselected_signal_assignment node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTselected_waveforms node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsensitivity_list node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsequence_of_statements node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsimple_expression node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsign node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsignal_assignment_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsignal_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsignal_kind node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsignal_list node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsignature node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTterm node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTslice_name node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsubprogram_body node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsubprogram_declarative_part node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsubprogram_specification node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsubprogram_statement_part node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsubtype_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsubtype_indication node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTfree_quantity_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTbranch_quantity_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsource_quantity_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTacross_aspect node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTthrough_aspect node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsource_aspect node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTtype_conversion node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTunconstrained_array_definition node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTuse_clause node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTvariable_assignment_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTvariable_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTwait_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTwaveform node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTwaveform_element node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsimultaneous_statement_part node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsimple_simultaneous_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsimultaneous_if_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsimultaneous_case_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsimultaneous_alternative node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsimultaneous_procedural_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTprocedural_declarative_part node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTprocedural_statement_part node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTsimultaneous_null_statement node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTshared_variable_declaration node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }

  @Override
  public Result visit(ASTerror_skipto node, Argument data) throws Failure {
    return visit((SimpleNode) node, data);
  }
}
