package stupaq.translation.lv2vhdl.parsing;

import com.google.common.reflect.Reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import stupaq.translation.errors.SyntaxException;
import stupaq.translation.semantic.FlattenNestedListsVisitor;
import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.VHDL93Parser;
import stupaq.vhdl93.VHDL93ParserTotal;
import stupaq.vhdl93.ast.*;

public interface VHDL93ParserPartial extends VHDL93Parser {
  public static final Logger LOGGER = LoggerFactory.getLogger(VHDL93ParserPartial.class);

  @Override
  eof eof();

  @Override
  label label();

  @Override
  name name();

  @Override
  name_extension name_extension();

  @Override
  identifier identifier();

  @Override
  identifier_list identifier_list();

  @Override
  association_list association_list();

  @Override
  named_association_list named_association_list();

  @Override
  positional_association_list positional_association_list();

  @Override
  named_association_element named_association_element();

  @Override
  positional_association_element positional_association_element();

  @Override
  formal_part formal_part();

  @Override
  actual_part actual_part();

  @Override
  actual_part_open actual_part_open();

  @Override
  expression expression();

  @Override
  constant_expression constant_expression();

  @Override
  logical_operator logical_operator();

  @Override
  relation_expression relation_expression();

  @Override
  constant_relation_expression constant_relation_expression();

  @Override
  relational_operator relational_operator();

  @Override
  shift_expression shift_expression();

  @Override
  constant_shift_expression constant_shift_expression();

  @Override
  shift_operator shift_operator();

  @Override
  simple_expression simple_expression();

  @Override
  constant_simple_expression constant_simple_expression();

  @Override
  sign sign();

  @Override
  adding_operator adding_operator();

  @Override
  term term();

  @Override
  constant_term constant_term();

  @Override
  multiplying_operator multiplying_operator();

  @Override
  factor factor();

  @Override
  constant_factor constant_factor();

  @Override
  primary primary();

  @Override
  constant_primary constant_primary();

  @Override
  name_expression name_expression();

  @Override
  access_type_definition access_type_definition();

  @Override
  array_type_definition array_type_definition();

  @Override
  composite_type_definition composite_type_definition();

  @Override
  element_subtype_definition element_subtype_definition();

  @Override
  enumeration_type_definition enumeration_type_definition();

  @Override
  file_type_definition file_type_definition();

  @Override
  floating_type_definition floating_type_definition();

  @Override
  full_type_declaration full_type_declaration();

  @Override
  incomplete_type_declaration incomplete_type_declaration();

  @Override
  index_subtype_definition index_subtype_definition();

  @Override
  integer_type_definition integer_type_definition();

  @Override
  physical_type_definition physical_type_definition();

  @Override
  physical_type_identifier physical_type_identifier();

  @Override
  record_type_definition record_type_definition();

  @Override
  record_type_identifier record_type_identifier();

  @Override
  scalar_type_definition scalar_type_definition();

  @Override
  subtype_declaration subtype_declaration();

  @Override
  subtype_indication subtype_indication();

  @Override
  resolution_function_name resolution_function_name();

  @Override
  type_conversion type_conversion();

  @Override
  type_declaration type_declaration();

  @Override
  type_definition type_definition();

  @Override
  type_name type_name();

  @Override
  unconstrained_array_definition unconstrained_array_definition();

  @Override
  design_file design_file();

  @Override
  design_unit design_unit();

  @Override
  context_clause context_clause();

  @Override
  context_item context_item();

  @Override
  library_clause library_clause();

  @Override
  library_clause_item library_clause_item();

  @Override
  use_clause use_clause();

  @Override
  use_clause_item use_clause_item();

  @Override
  library_unit library_unit();

  @Override
  primary_unit primary_unit();

  @Override
  secondary_unit secondary_unit();

  @Override
  entity_declaration entity_declaration();

  @Override
  entity_header entity_header();

  @Override
  entity_declarative_part entity_declarative_part();

  @Override
  entity_declarative_item entity_declarative_item();

  @Override
  entity_statement_part entity_statement_part();

  @Override
  entity_statement entity_statement();

  @Override
  entity_identifier entity_identifier();

  @Override
  entity_name entity_name();

  @Override
  component_configuration component_configuration();

  @Override
  component_declaration component_declaration();

  @Override
  component_identifier component_identifier();

  @Override
  component_header component_header();

  @Override
  component_specification component_specification();

  @Override
  architecture_declaration architecture_declaration();

  @Override
  architecture_declarative_part architecture_declarative_part();

  @Override
  architecture_statement_part architecture_statement_part();

  @Override
  architecture_identifier architecture_identifier();

  @Override
  architecture_name architecture_name();

  @Override
  concurrent_statement concurrent_statement();

  @Override
  component_instantiation_statement component_instantiation_statement();

  @Override
  instantiated_unit instantiated_unit();

  @Override
  generic_map_aspect generic_map_aspect();

  @Override
  port_map_aspect port_map_aspect();

  @Override
  process_statement process_statement();

  @Override
  sensitivity_list sensitivity_list();

  @Override
  process_declarative_part process_declarative_part();

  @Override
  process_declarative_item process_declarative_item();

  @Override
  process_statement_part process_statement_part();

  @Override
  sequential_statement sequential_statement();

  @Override
  null_statement null_statement();

  @Override
  abstract_literal abstract_literal();

  @Override
  actual_parameter_part actual_parameter_part();

  @Override
  aggregate aggregate();

  @Override
  alias_declaration alias_declaration();

  @Override
  alias_designator alias_designator();

  @Override
  allocator allocator();

  @Override
  assertion assertion();

  @Override
  assertion_statement assertion_statement();

  @Override
  attribute_declaration attribute_declaration();

  @Override
  attribute_designator attribute_designator();

  @Override
  attribute_name attribute_name();

  @Override
  attribute_specification attribute_specification();

  @Override
  base_unit_declaration base_unit_declaration();

  @Override
  binding_indication binding_indication();

  @Override
  block_configuration block_configuration();

  @Override
  block_declarative_item block_declarative_item();

  @Override
  block_declarative_part block_declarative_part();

  @Override
  block_header block_header();

  @Override
  block_specification block_specification();

  @Override
  block_statement block_statement();

  @Override
  block_statement_part block_statement_part();

  @Override
  case_statement case_statement();

  @Override
  case_statement_alternative case_statement_alternative();

  @Override
  choice choice();

  @Override
  choices choices();

  @Override
  concurrent_assertion_statement concurrent_assertion_statement();

  @Override
  concurrent_procedure_call_statement concurrent_procedure_call_statement();

  @Override
  concurrent_signal_assignment_statement concurrent_signal_assignment_statement();

  @Override
  condition condition();

  @Override
  condition_clause condition_clause();

  @Override
  conditional_signal_assignment conditional_signal_assignment();

  @Override
  conditional_waveforms conditional_waveforms();

  @Override
  configuration_declaration configuration_declaration();

  @Override
  configuration_declarative_item configuration_declarative_item();

  @Override
  configuration_declarative_part configuration_declarative_part();

  @Override
  configuration_item configuration_item();

  @Override
  configuration_specification configuration_specification();

  @Override
  constant_declaration constant_declaration();

  @Override
  constrained_array_definition constrained_array_definition();

  @Override
  constraint constraint();

  @Override
  delay_mechanism delay_mechanism();

  @Override
  designator designator();

  @Override
  direction direction();

  @Override
  disconnection_specification disconnection_specification();

  @Override
  discrete_range discrete_range();

  @Override
  element_association element_association();

  @Override
  element_declaration element_declaration();

  @Override
  entity_aspect entity_aspect();

  @Override
  shared_variable_declaration shared_variable_declaration();

  @Override
  entity_class entity_class();

  @Override
  entity_class_entry entity_class_entry();

  @Override
  entity_class_entry_list entity_class_entry_list();

  @Override
  entity_designator entity_designator();

  @Override
  entity_name_list entity_name_list();

  @Override
  entity_specification entity_specification();

  @Override
  entity_tag entity_tag();

  @Override
  enumeration_literal enumeration_literal();

  @Override
  exit_statement exit_statement();

  @Override
  file_declaration file_declaration();

  @Override
  file_logical_name file_logical_name();

  @Override
  file_open_information file_open_information();

  @Override
  formal_parameter_list formal_parameter_list();

  @Override
  function_call function_call();

  @Override
  generate_statement generate_statement();

  @Override
  generation_scheme generation_scheme();

  @Override
  generic_clause generic_clause();

  @Override
  generic_list generic_list();

  @Override
  generic_interface_list generic_interface_list();

  @Override
  group_constituent group_constituent();

  @Override
  group_constituent_list group_constituent_list();

  @Override
  group_template_declaration group_template_declaration();

  @Override
  group_declaration group_declaration();

  @Override
  guarded_signal_specification guarded_signal_specification();

  @Override
  if_statement if_statement();

  @Override
  index_constraint index_constraint();

  @Override
  index_specification index_specification();

  @Override
  indexed_name indexed_name();

  @Override
  instantiation_list instantiation_list();

  @Override
  interface_constant_declaration interface_constant_declaration();

  @Override
  interface_declaration interface_declaration();

  @Override
  interface_element interface_element();

  @Override
  interface_file_declaration interface_file_declaration();

  @Override
  interface_list interface_list();

  @Override
  interface_signal_declaration interface_signal_declaration();

  @Override
  interface_variable_declaration interface_variable_declaration();

  @Override
  iteration_scheme iteration_scheme();

  @Override
  literal literal();

  @Override
  loop_statement loop_statement();

  @Override
  miscellaneous_operator miscellaneous_operator();

  @Override
  mode mode();

  @Override
  next_statement next_statement();

  @Override
  numeric_literal numeric_literal();

  @Override
  object_declaration object_declaration();

  @Override
  operator_symbol operator_symbol();

  @Override
  options_ options_();

  @Override
  package_body package_body();

  @Override
  package_body_declarative_item package_body_declarative_item();

  @Override
  package_body_declarative_part package_body_declarative_part();

  @Override
  package_declaration package_declaration();

  @Override
  package_declarative_item package_declarative_item();

  @Override
  package_declarative_part package_declarative_part();

  @Override
  parameter_specification parameter_specification();

  @Override
  physical_literal physical_literal();

  @Override
  port_clause port_clause();

  @Override
  port_list port_list();

  @Override
  port_interface_list port_interface_list();

  @Override
  prefix prefix();

  @Override
  procedure_call procedure_call();

  @Override
  procedure_call_statement procedure_call_statement();

  @Override
  qualified_expression qualified_expression();

  @Override
  range range();

  @Override
  range_attribute_value range_attribute_value();

  @Override
  range_constraint range_constraint();

  @Override
  report_statement report_statement();

  @Override
  return_statement return_statement();

  @Override
  secondary_unit_declaration secondary_unit_declaration();

  @Override
  selected_signal_assignment selected_signal_assignment();

  @Override
  selected_waveforms selected_waveforms();

  @Override
  sensitivity_clause sensitivity_clause();

  @Override
  sequence_of_statements sequence_of_statements();

  @Override
  signal_assignment_statement signal_assignment_statement();

  @Override
  signal_declaration signal_declaration();

  @Override
  signal_kind signal_kind();

  @Override
  signal_list signal_list();

  @Override
  signature signature();

  @Override
  slice_name slice_name();

  @Override
  subprogram_body subprogram_body();

  @Override
  subprogram_declaration subprogram_declaration();

  @Override
  subprogram_declarative_item subprogram_declarative_item();

  @Override
  subprogram_declarative_part subprogram_declarative_part();

  @Override
  subprogram_kind subprogram_kind();

  @Override
  subprogram_specification subprogram_specification();

  @Override
  subprogram_statement_part subprogram_statement_part();

  @Override
  suffix suffix();

  @Override
  target target();

  @Override
  timeout_clause timeout_clause();

  @Override
  variable_assignment_statement variable_assignment_statement();

  @Override
  variable_declaration variable_declaration();

  @Override
  wait_statement wait_statement();

  @Override
  waveform waveform();

  @Override
  waveform_element waveform_element();

  @Override
  block_label block_label();

  @Override
  block_statement_label block_statement_label();

  @Override
  case_label case_label();

  @Override
  generate_label generate_label();

  @Override
  generate_statement_label generate_statement_label();

  @Override
  if_label if_label();

  @Override
  instantiation_label instantiation_label();

  @Override
  loop_label loop_label();

  @Override
  process_label process_label();

  @Override
  attribute_simple_name attribute_simple_name();

  @Override
  component_simple_name component_simple_name();

  @Override
  configuration_simple_name configuration_simple_name();

  @Override
  element_simple_name element_simple_name();

  @Override
  entity_simple_name entity_simple_name();

  @Override
  package_simple_name package_simple_name();

  @Override
  file_name file_name();

  @Override
  function_name function_name();

  @Override
  configuration_name configuration_name();

  @Override
  component_name component_name();

  @Override
  generic_name generic_name();

  @Override
  group_template_name group_template_name();

  @Override
  parameter_name parameter_name();

  @Override
  port_name port_name();

  @Override
  procedure_name procedure_name();

  @Override
  range_attribute_name range_attribute_name();

  @Override
  signal_name signal_name();

  @Override
  unit_name unit_name();

  @Override
  variable_name variable_name();

  @Override
  static_expression static_expression();

  @Override
  boolean_expression boolean_expression();

  @Override
  file_open_kind_expression file_open_kind_expression();

  @Override
  guard_expression guard_expression();

  @Override
  time_expression time_expression();

  @Override
  value_expression value_expression();

  @Override
  string_expression string_expression();

  @Override
  guarded_signal_list guarded_signal_list();

  @Override
  parameter_association_list parameter_association_list();

  @Override
  parameter_interface_list parameter_interface_list();

  @Override
  formal_port_clause formal_port_clause();

  @Override
  local_port_clause local_port_clause();

  @Override
  formal_generic_clause formal_generic_clause();

  @Override
  local_generic_clause local_generic_clause();

  @Override
  element_subtype_indication element_subtype_indication();

  @Override
  discrete_subtype_indication discrete_subtype_indication();

  @Override
  loop_parameter_specification loop_parameter_specification();

  @Override
  generate_parameter_specification generate_parameter_specification();

  @Override
  passive_concurrent_procedure_call_statement passive_concurrent_procedure_call_statement();

  @Override
  passive_process_statement passive_process_statement();

  @Override
  magnitude_simple_expression magnitude_simple_expression();

  @Override
  phase_simple_expression phase_simple_expression();

  public static final class Parsers {
    private Parsers() {
    }

    public static VHDL93ParserPartial forString(String string) {
      LOGGER.trace("Parsing: {}", string);
      return Reflection.newProxy(VHDL93ParserPartial.class, new ParserHandler(string));
    }

    private static final class ParserHandler implements InvocationHandler {
      private final VHDL93ParserTotal parser;

      public ParserHandler(String string) {
        parser = new VHDL93ParserTotal(new StringReader(string));
      }

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
          Object result = method.invoke(parser, args);
          parser.eof();
          if (result instanceof Node) {
            ((Node) result).accept(new FlattenNestedListsVisitor());
          }
          return result;
        } catch (InvocationTargetException e) {
          Throwable t = e.getTargetException();
          if (t instanceof ParseException) {
            throw new SyntaxException((ParseException) t);
          } else {
            throw e;
          }
        }
      }
    }
  }
}
