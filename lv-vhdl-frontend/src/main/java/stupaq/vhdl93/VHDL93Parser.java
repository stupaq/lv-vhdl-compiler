package stupaq.vhdl93;

import stupaq.vhdl93.ast.*;

public interface VHDL93Parser {
  eof eof() throws ParseException;

  label label() throws ParseException;

  name name() throws ParseException;

  name_extension name_extension() throws ParseException;

  identifier identifier() throws ParseException;

  identifier_list identifier_list() throws ParseException;

  association_list association_list() throws ParseException;

  named_association_list named_association_list() throws ParseException;

  positional_association_list positional_association_list() throws ParseException;

  named_association_element named_association_element() throws ParseException;

  positional_association_element positional_association_element() throws ParseException;

  formal_part formal_part() throws ParseException;

  actual_part actual_part() throws ParseException;

  actual_part_open actual_part_open() throws ParseException;

  expression expression() throws ParseException;

  constant_expression constant_expression() throws ParseException;

  logical_operator logical_operator() throws ParseException;

  relation_expression relation_expression() throws ParseException;

  constant_relation_expression constant_relation_expression() throws ParseException;

  relational_operator relational_operator() throws ParseException;

  shift_expression shift_expression() throws ParseException;

  constant_shift_expression constant_shift_expression() throws ParseException;

  shift_operator shift_operator() throws ParseException;

  simple_expression simple_expression() throws ParseException;

  constant_simple_expression constant_simple_expression() throws ParseException;

  sign sign() throws ParseException;

  adding_operator adding_operator() throws ParseException;

  term term() throws ParseException;

  constant_term constant_term() throws ParseException;

  multiplying_operator multiplying_operator() throws ParseException;

  factor factor() throws ParseException;

  constant_factor constant_factor() throws ParseException;

  primary primary() throws ParseException;

  constant_primary constant_primary() throws ParseException;

  name_expression name_expression() throws ParseException;

  access_type_definition access_type_definition() throws ParseException;

  array_type_definition array_type_definition() throws ParseException;

  composite_type_definition composite_type_definition() throws ParseException;

  element_subtype_definition element_subtype_definition() throws ParseException;

  enumeration_type_definition enumeration_type_definition() throws ParseException;

  file_type_definition file_type_definition() throws ParseException;

  floating_type_definition floating_type_definition() throws ParseException;

  full_type_declaration full_type_declaration() throws ParseException;

  incomplete_type_declaration incomplete_type_declaration() throws ParseException;

  index_subtype_definition index_subtype_definition() throws ParseException;

  integer_type_definition integer_type_definition() throws ParseException;

  physical_type_definition physical_type_definition() throws ParseException;

  physical_type_identifier physical_type_identifier() throws ParseException;

  record_type_definition record_type_definition() throws ParseException;

  record_type_identifier record_type_identifier() throws ParseException;

  scalar_type_definition scalar_type_definition() throws ParseException;

  subtype_declaration subtype_declaration() throws ParseException;

  subtype_indication subtype_indication() throws ParseException;

  resolution_function_name resolution_function_name() throws ParseException;

  type_conversion type_conversion() throws ParseException;

  type_declaration type_declaration() throws ParseException;

  type_definition type_definition() throws ParseException;

  type_name type_name() throws ParseException;

  unconstrained_array_definition unconstrained_array_definition() throws ParseException;

  design_file design_file() throws ParseException;

  design_unit design_unit() throws ParseException;

  context_clause context_clause() throws ParseException;

  context_item context_item() throws ParseException;

  library_clause library_clause() throws ParseException;

  library_clause_item library_clause_item() throws ParseException;

  use_clause use_clause() throws ParseException;

  use_clause_item use_clause_item() throws ParseException;

  library_unit library_unit() throws ParseException;

  primary_unit primary_unit() throws ParseException;

  secondary_unit secondary_unit() throws ParseException;

  entity_declaration entity_declaration() throws ParseException;

  entity_header entity_header() throws ParseException;

  entity_declarative_part entity_declarative_part() throws ParseException;

  entity_declarative_item entity_declarative_item() throws ParseException;

  entity_statement_part entity_statement_part() throws ParseException;

  entity_statement entity_statement() throws ParseException;

  entity_identifier entity_identifier() throws ParseException;

  entity_name entity_name() throws ParseException;

  component_configuration component_configuration() throws ParseException;

  component_declaration component_declaration() throws ParseException;

  component_identifier component_identifier() throws ParseException;

  component_header component_header() throws ParseException;

  component_specification component_specification() throws ParseException;

  architecture_declaration architecture_declaration() throws ParseException;

  architecture_declarative_part architecture_declarative_part() throws ParseException;

  architecture_statement_part architecture_statement_part() throws ParseException;

  architecture_identifier architecture_identifier() throws ParseException;

  architecture_name architecture_name() throws ParseException;

  concurrent_statement concurrent_statement() throws ParseException;

  component_instantiation_statement component_instantiation_statement() throws ParseException;

  instantiated_unit instantiated_unit() throws ParseException;

  generic_map_aspect generic_map_aspect() throws ParseException;

  port_map_aspect port_map_aspect() throws ParseException;

  process_statement process_statement() throws ParseException;

  sensitivity_list sensitivity_list() throws ParseException;

  process_declarative_part process_declarative_part() throws ParseException;

  process_declarative_item process_declarative_item() throws ParseException;

  process_statement_part process_statement_part() throws ParseException;

  sequential_statement sequential_statement() throws ParseException;

  null_statement null_statement() throws ParseException;

  abstract_literal abstract_literal() throws ParseException;

  actual_parameter_part actual_parameter_part() throws ParseException;

  aggregate aggregate() throws ParseException;

  alias_declaration alias_declaration() throws ParseException;

  alias_designator alias_designator() throws ParseException;

  allocator allocator() throws ParseException;

  assertion assertion() throws ParseException;

  assertion_statement assertion_statement() throws ParseException;

  attribute_declaration attribute_declaration() throws ParseException;

  attribute_designator attribute_designator() throws ParseException;

  attribute_name attribute_name() throws ParseException;

  attribute_specification attribute_specification() throws ParseException;

  base_unit_declaration base_unit_declaration() throws ParseException;

  binding_indication binding_indication() throws ParseException;

  block_configuration block_configuration() throws ParseException;

  block_declarative_item block_declarative_item() throws ParseException;

  block_declarative_part block_declarative_part() throws ParseException;

  block_header block_header() throws ParseException;

  block_specification block_specification() throws ParseException;

  block_statement block_statement() throws ParseException;

  block_statement_part block_statement_part() throws ParseException;

  case_statement case_statement() throws ParseException;

  case_statement_alternative case_statement_alternative() throws ParseException;

  choice choice() throws ParseException;

  choices choices() throws ParseException;

  concurrent_assertion_statement concurrent_assertion_statement() throws ParseException;

  concurrent_procedure_call_statement concurrent_procedure_call_statement() throws ParseException;

  concurrent_signal_assignment_statement concurrent_signal_assignment_statement() throws ParseException;

  condition condition() throws ParseException;

  condition_clause condition_clause() throws ParseException;

  conditional_signal_assignment conditional_signal_assignment() throws ParseException;

  conditional_waveforms conditional_waveforms() throws ParseException;

  configuration_declaration configuration_declaration() throws ParseException;

  configuration_declarative_item configuration_declarative_item() throws ParseException;

  configuration_declarative_part configuration_declarative_part() throws ParseException;

  configuration_item configuration_item() throws ParseException;

  configuration_specification configuration_specification() throws ParseException;

  constant_declaration constant_declaration() throws ParseException;

  constrained_array_definition constrained_array_definition() throws ParseException;

  constraint constraint() throws ParseException;

  delay_mechanism delay_mechanism() throws ParseException;

  designator designator() throws ParseException;

  direction direction() throws ParseException;

  disconnection_specification disconnection_specification() throws ParseException;

  discrete_range discrete_range() throws ParseException;

  element_association element_association() throws ParseException;

  element_declaration element_declaration() throws ParseException;

  entity_aspect entity_aspect() throws ParseException;

  shared_variable_declaration shared_variable_declaration() throws ParseException;

  entity_class entity_class() throws ParseException;

  entity_class_entry entity_class_entry() throws ParseException;

  entity_class_entry_list entity_class_entry_list() throws ParseException;

  entity_designator entity_designator() throws ParseException;

  entity_name_list entity_name_list() throws ParseException;

  entity_specification entity_specification() throws ParseException;

  entity_tag entity_tag() throws ParseException;

  enumeration_literal enumeration_literal() throws ParseException;

  exit_statement exit_statement() throws ParseException;

  file_declaration file_declaration() throws ParseException;

  file_logical_name file_logical_name() throws ParseException;

  file_open_information file_open_information() throws ParseException;

  formal_parameter_list formal_parameter_list() throws ParseException;

  function_call function_call() throws ParseException;

  generate_statement generate_statement() throws ParseException;

  generation_scheme generation_scheme() throws ParseException;

  generic_clause generic_clause() throws ParseException;

  generic_list generic_list() throws ParseException;

  generic_interface_list generic_interface_list() throws ParseException;

  group_constituent group_constituent() throws ParseException;

  group_constituent_list group_constituent_list() throws ParseException;

  group_template_declaration group_template_declaration() throws ParseException;

  group_declaration group_declaration() throws ParseException;

  guarded_signal_specification guarded_signal_specification() throws ParseException;

  if_statement if_statement() throws ParseException;

  index_constraint index_constraint() throws ParseException;

  index_specification index_specification() throws ParseException;

  indexed_name indexed_name() throws ParseException;

  instantiation_list instantiation_list() throws ParseException;

  interface_constant_declaration interface_constant_declaration() throws ParseException;

  interface_declaration interface_declaration() throws ParseException;

  interface_element interface_element() throws ParseException;

  interface_file_declaration interface_file_declaration() throws ParseException;

  interface_list interface_list() throws ParseException;

  interface_signal_declaration interface_signal_declaration() throws ParseException;

  interface_variable_declaration interface_variable_declaration() throws ParseException;

  iteration_scheme iteration_scheme() throws ParseException;

  literal literal() throws ParseException;

  loop_statement loop_statement() throws ParseException;

  miscellaneous_operator miscellaneous_operator() throws ParseException;

  mode mode() throws ParseException;

  next_statement next_statement() throws ParseException;

  numeric_literal numeric_literal() throws ParseException;

  object_declaration object_declaration() throws ParseException;

  operator_symbol operator_symbol() throws ParseException;

  options_ options_() throws ParseException;

  package_body package_body() throws ParseException;

  package_body_declarative_item package_body_declarative_item() throws ParseException;

  package_body_declarative_part package_body_declarative_part() throws ParseException;

  package_declaration package_declaration() throws ParseException;

  package_declarative_item package_declarative_item() throws ParseException;

  package_declarative_part package_declarative_part() throws ParseException;

  parameter_specification parameter_specification() throws ParseException;

  physical_literal physical_literal() throws ParseException;

  port_clause port_clause() throws ParseException;

  port_list port_list() throws ParseException;

  port_interface_list port_interface_list() throws ParseException;

  prefix prefix() throws ParseException;

  procedure_call procedure_call() throws ParseException;

  procedure_call_statement procedure_call_statement() throws ParseException;

  qualified_expression qualified_expression() throws ParseException;

  range range() throws ParseException;

  range_attribute_value range_attribute_value() throws ParseException;

  range_constraint range_constraint() throws ParseException;

  report_statement report_statement() throws ParseException;

  return_statement return_statement() throws ParseException;

  secondary_unit_declaration secondary_unit_declaration() throws ParseException;

  selected_signal_assignment selected_signal_assignment() throws ParseException;

  selected_waveforms selected_waveforms() throws ParseException;

  sensitivity_clause sensitivity_clause() throws ParseException;

  sequence_of_statements sequence_of_statements() throws ParseException;

  signal_assignment_statement signal_assignment_statement() throws ParseException;

  signal_declaration signal_declaration() throws ParseException;

  signal_kind signal_kind() throws ParseException;

  signal_list signal_list() throws ParseException;

  signature signature() throws ParseException;

  slice_name slice_name() throws ParseException;

  subprogram_body subprogram_body() throws ParseException;

  subprogram_declaration subprogram_declaration() throws ParseException;

  subprogram_declarative_item subprogram_declarative_item() throws ParseException;

  subprogram_declarative_part subprogram_declarative_part() throws ParseException;

  subprogram_kind subprogram_kind() throws ParseException;

  subprogram_specification subprogram_specification() throws ParseException;

  subprogram_statement_part subprogram_statement_part() throws ParseException;

  suffix suffix() throws ParseException;

  target target() throws ParseException;

  timeout_clause timeout_clause() throws ParseException;

  variable_assignment_statement variable_assignment_statement() throws ParseException;

  variable_declaration variable_declaration() throws ParseException;

  wait_statement wait_statement() throws ParseException;

  waveform waveform() throws ParseException;

  waveform_element waveform_element() throws ParseException;

  block_label block_label() throws ParseException;

  block_statement_label block_statement_label() throws ParseException;

  case_label case_label() throws ParseException;

  generate_label generate_label() throws ParseException;

  generate_statement_label generate_statement_label() throws ParseException;

  if_label if_label() throws ParseException;

  instantiation_label instantiation_label() throws ParseException;

  loop_label loop_label() throws ParseException;

  process_label process_label() throws ParseException;

  attribute_simple_name attribute_simple_name() throws ParseException;

  component_simple_name component_simple_name() throws ParseException;

  configuration_simple_name configuration_simple_name() throws ParseException;

  element_simple_name element_simple_name() throws ParseException;

  entity_simple_name entity_simple_name() throws ParseException;

  package_simple_name package_simple_name() throws ParseException;

  file_name file_name() throws ParseException;

  function_name function_name() throws ParseException;

  configuration_name configuration_name() throws ParseException;

  component_name component_name() throws ParseException;

  generic_name generic_name() throws ParseException;

  group_template_name group_template_name() throws ParseException;

  parameter_name parameter_name() throws ParseException;

  port_name port_name() throws ParseException;

  procedure_name procedure_name() throws ParseException;

  range_attribute_name range_attribute_name() throws ParseException;

  signal_name signal_name() throws ParseException;

  unit_name unit_name() throws ParseException;

  variable_name variable_name() throws ParseException;

  static_expression static_expression() throws ParseException;

  boolean_expression boolean_expression() throws ParseException;

  file_open_kind_expression file_open_kind_expression() throws ParseException;

  guard_expression guard_expression() throws ParseException;

  time_expression time_expression() throws ParseException;

  value_expression value_expression() throws ParseException;

  string_expression string_expression() throws ParseException;

  guarded_signal_list guarded_signal_list() throws ParseException;

  parameter_association_list parameter_association_list() throws ParseException;

  parameter_interface_list parameter_interface_list() throws ParseException;

  formal_port_clause formal_port_clause() throws ParseException;

  local_port_clause local_port_clause() throws ParseException;

  formal_generic_clause formal_generic_clause() throws ParseException;

  local_generic_clause local_generic_clause() throws ParseException;

  element_subtype_indication element_subtype_indication() throws ParseException;

  discrete_subtype_indication discrete_subtype_indication() throws ParseException;

  loop_parameter_specification loop_parameter_specification() throws ParseException;

  generate_parameter_specification generate_parameter_specification() throws ParseException;

  passive_concurrent_procedure_call_statement passive_concurrent_procedure_call_statement() throws ParseException;

  passive_process_statement passive_process_statement() throws ParseException;

  magnitude_simple_expression magnitude_simple_expression() throws ParseException;

  phase_simple_expression phase_simple_expression() throws ParseException;
}
