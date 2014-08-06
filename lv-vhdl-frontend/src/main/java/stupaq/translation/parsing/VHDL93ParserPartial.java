package stupaq.translation.parsing;

import com.google.common.reflect.Reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import stupaq.translation.errors.SemanticException;
import stupaq.translation.errors.SyntaxException;
import stupaq.translation.semantic.FlattenNestedListsVisitor;
import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.VHDL93Parser;
import stupaq.vhdl93.VHDL93ParserTotal;
import stupaq.vhdl93.ast.*;

public interface VHDL93ParserPartial extends VHDL93Parser {
  public static final Logger LOGGER = LoggerFactory.getLogger(VHDL93ParserPartial.class);

  @Override
  eof eof() throws SemanticException;

  @Override
  label label() throws SemanticException;

  @Override
  name name() throws SemanticException;

  @Override
  name_extension name_extension() throws SemanticException;

  @Override
  identifier identifier() throws SemanticException;

  @Override
  identifier_list identifier_list() throws SemanticException;

  @Override
  association_list association_list() throws SemanticException;

  @Override
  named_association_list named_association_list() throws SemanticException;

  @Override
  positional_association_list positional_association_list() throws SemanticException;

  @Override
  named_association_element named_association_element() throws SemanticException;

  @Override
  positional_association_element positional_association_element() throws SemanticException;

  @Override
  formal_part formal_part() throws SemanticException;

  @Override
  actual_part actual_part() throws SemanticException;

  @Override
  actual_part_open actual_part_open() throws SemanticException;

  @Override
  expression expression() throws SemanticException;

  @Override
  constant_expression constant_expression() throws SemanticException;

  @Override
  logical_operator logical_operator() throws SemanticException;

  @Override
  relation_expression relation_expression() throws SemanticException;

  @Override
  constant_relation_expression constant_relation_expression() throws SemanticException;

  @Override
  relational_operator relational_operator() throws SemanticException;

  @Override
  shift_expression shift_expression() throws SemanticException;

  @Override
  constant_shift_expression constant_shift_expression() throws SemanticException;

  @Override
  shift_operator shift_operator() throws SemanticException;

  @Override
  simple_expression simple_expression() throws SemanticException;

  @Override
  constant_simple_expression constant_simple_expression() throws SemanticException;

  @Override
  sign sign() throws SemanticException;

  @Override
  adding_operator adding_operator() throws SemanticException;

  @Override
  term term() throws SemanticException;

  @Override
  constant_term constant_term() throws SemanticException;

  @Override
  multiplying_operator multiplying_operator() throws SemanticException;

  @Override
  factor factor() throws SemanticException;

  @Override
  constant_factor constant_factor() throws SemanticException;

  @Override
  primary primary() throws SemanticException;

  @Override
  constant_primary constant_primary() throws SemanticException;

  @Override
  name_expression name_expression() throws SemanticException;

  @Override
  access_type_definition access_type_definition() throws SemanticException;

  @Override
  array_type_definition array_type_definition() throws SemanticException;

  @Override
  composite_type_definition composite_type_definition() throws SemanticException;

  @Override
  element_subtype_definition element_subtype_definition() throws SemanticException;

  @Override
  enumeration_type_definition enumeration_type_definition() throws SemanticException;

  @Override
  file_type_definition file_type_definition() throws SemanticException;

  @Override
  floating_type_definition floating_type_definition() throws SemanticException;

  @Override
  full_type_declaration full_type_declaration() throws SemanticException;

  @Override
  incomplete_type_declaration incomplete_type_declaration() throws SemanticException;

  @Override
  index_subtype_definition index_subtype_definition() throws SemanticException;

  @Override
  integer_type_definition integer_type_definition() throws SemanticException;

  @Override
  physical_type_definition physical_type_definition() throws SemanticException;

  @Override
  physical_type_identifier physical_type_identifier() throws SemanticException;

  @Override
  record_type_definition record_type_definition() throws SemanticException;

  @Override
  record_type_identifier record_type_identifier() throws SemanticException;

  @Override
  scalar_type_definition scalar_type_definition() throws SemanticException;

  @Override
  subtype_declaration subtype_declaration() throws SemanticException;

  @Override
  subtype_indication subtype_indication() throws SemanticException;

  @Override
  resolution_function_name resolution_function_name() throws SemanticException;

  @Override
  type_conversion type_conversion() throws SemanticException;

  @Override
  type_declaration type_declaration() throws SemanticException;

  @Override
  type_definition type_definition() throws SemanticException;

  @Override
  type_name type_name() throws SemanticException;

  @Override
  unconstrained_array_definition unconstrained_array_definition() throws SemanticException;

  @Override
  design_file design_file() throws SemanticException;

  @Override
  design_unit design_unit() throws SemanticException;

  @Override
  context_clause context_clause() throws SemanticException;

  @Override
  context_item context_item() throws SemanticException;

  @Override
  library_clause library_clause() throws SemanticException;

  @Override
  library_clause_item library_clause_item() throws SemanticException;

  @Override
  use_clause use_clause() throws SemanticException;

  @Override
  use_clause_item use_clause_item() throws SemanticException;

  @Override
  library_unit library_unit() throws SemanticException;

  @Override
  primary_unit primary_unit() throws SemanticException;

  @Override
  secondary_unit secondary_unit() throws SemanticException;

  @Override
  entity_declaration entity_declaration() throws SemanticException;

  @Override
  entity_header entity_header() throws SemanticException;

  @Override
  entity_declarative_part entity_declarative_part() throws SemanticException;

  @Override
  entity_declarative_item entity_declarative_item() throws SemanticException;

  @Override
  entity_statement_part entity_statement_part() throws SemanticException;

  @Override
  entity_statement entity_statement() throws SemanticException;

  @Override
  entity_identifier entity_identifier() throws SemanticException;

  @Override
  entity_name entity_name() throws SemanticException;

  @Override
  component_configuration component_configuration() throws SemanticException;

  @Override
  component_declaration component_declaration() throws SemanticException;

  @Override
  component_identifier component_identifier() throws SemanticException;

  @Override
  component_header component_header() throws SemanticException;

  @Override
  component_specification component_specification() throws SemanticException;

  @Override
  architecture_declaration architecture_declaration() throws SemanticException;

  @Override
  architecture_declarative_part architecture_declarative_part() throws SemanticException;

  @Override
  architecture_statement_part architecture_statement_part() throws SemanticException;

  @Override
  architecture_identifier architecture_identifier() throws SemanticException;

  @Override
  architecture_name architecture_name() throws SemanticException;

  @Override
  concurrent_statement concurrent_statement() throws SemanticException;

  @Override
  component_instantiation_statement component_instantiation_statement() throws SemanticException;

  @Override
  instantiated_unit instantiated_unit() throws SemanticException;

  @Override
  generic_map_aspect generic_map_aspect() throws SemanticException;

  @Override
  port_map_aspect port_map_aspect() throws SemanticException;

  @Override
  process_statement process_statement() throws SemanticException;

  @Override
  sensitivity_list sensitivity_list() throws SemanticException;

  @Override
  process_declarative_part process_declarative_part() throws SemanticException;

  @Override
  process_declarative_item process_declarative_item() throws SemanticException;

  @Override
  process_statement_part process_statement_part() throws SemanticException;

  @Override
  sequential_statement sequential_statement() throws SemanticException;

  @Override
  null_statement null_statement() throws SemanticException;

  @Override
  abstract_literal abstract_literal() throws SemanticException;

  @Override
  actual_parameter_part actual_parameter_part() throws SemanticException;

  @Override
  aggregate aggregate() throws SemanticException;

  @Override
  alias_declaration alias_declaration() throws SemanticException;

  @Override
  alias_designator alias_designator() throws SemanticException;

  @Override
  allocator allocator() throws SemanticException;

  @Override
  assertion assertion() throws SemanticException;

  @Override
  assertion_statement assertion_statement() throws SemanticException;

  @Override
  attribute_declaration attribute_declaration() throws SemanticException;

  @Override
  attribute_designator attribute_designator() throws SemanticException;

  @Override
  attribute_name attribute_name() throws SemanticException;

  @Override
  attribute_specification attribute_specification() throws SemanticException;

  @Override
  base_unit_declaration base_unit_declaration() throws SemanticException;

  @Override
  binding_indication binding_indication() throws SemanticException;

  @Override
  block_configuration block_configuration() throws SemanticException;

  @Override
  block_declarative_item block_declarative_item() throws SemanticException;

  @Override
  block_declarative_part block_declarative_part() throws SemanticException;

  @Override
  block_header block_header() throws SemanticException;

  @Override
  block_specification block_specification() throws SemanticException;

  @Override
  block_statement block_statement() throws SemanticException;

  @Override
  block_statement_part block_statement_part() throws SemanticException;

  @Override
  case_statement case_statement() throws SemanticException;

  @Override
  case_statement_alternative case_statement_alternative() throws SemanticException;

  @Override
  choice choice() throws SemanticException;

  @Override
  choices choices() throws SemanticException;

  @Override
  concurrent_assertion_statement concurrent_assertion_statement() throws SemanticException;

  @Override
  concurrent_procedure_call_statement concurrent_procedure_call_statement()
      throws SemanticException;

  @Override
  concurrent_signal_assignment_statement concurrent_signal_assignment_statement()
      throws SemanticException;

  @Override
  condition condition() throws SemanticException;

  @Override
  condition_clause condition_clause() throws SemanticException;

  @Override
  conditional_signal_assignment conditional_signal_assignment() throws SemanticException;

  @Override
  conditional_waveforms conditional_waveforms() throws SemanticException;

  @Override
  configuration_declaration configuration_declaration() throws SemanticException;

  @Override
  configuration_declarative_item configuration_declarative_item() throws SemanticException;

  @Override
  configuration_declarative_part configuration_declarative_part() throws SemanticException;

  @Override
  configuration_item configuration_item() throws SemanticException;

  @Override
  configuration_specification configuration_specification() throws SemanticException;

  @Override
  constant_declaration constant_declaration() throws SemanticException;

  @Override
  constrained_array_definition constrained_array_definition() throws SemanticException;

  @Override
  constraint constraint() throws SemanticException;

  @Override
  delay_mechanism delay_mechanism() throws SemanticException;

  @Override
  designator designator() throws SemanticException;

  @Override
  direction direction() throws SemanticException;

  @Override
  disconnection_specification disconnection_specification() throws SemanticException;

  @Override
  discrete_range discrete_range() throws SemanticException;

  @Override
  element_association element_association() throws SemanticException;

  @Override
  element_declaration element_declaration() throws SemanticException;

  @Override
  entity_aspect entity_aspect() throws SemanticException;

  @Override
  shared_variable_declaration shared_variable_declaration() throws SemanticException;

  @Override
  entity_class entity_class() throws SemanticException;

  @Override
  entity_class_entry entity_class_entry() throws SemanticException;

  @Override
  entity_class_entry_list entity_class_entry_list() throws SemanticException;

  @Override
  entity_designator entity_designator() throws SemanticException;

  @Override
  entity_name_list entity_name_list() throws SemanticException;

  @Override
  entity_specification entity_specification() throws SemanticException;

  @Override
  entity_tag entity_tag() throws SemanticException;

  @Override
  enumeration_literal enumeration_literal() throws SemanticException;

  @Override
  exit_statement exit_statement() throws SemanticException;

  @Override
  file_declaration file_declaration() throws SemanticException;

  @Override
  file_logical_name file_logical_name() throws SemanticException;

  @Override
  file_open_information file_open_information() throws SemanticException;

  @Override
  formal_parameter_list formal_parameter_list() throws SemanticException;

  @Override
  function_call function_call() throws SemanticException;

  @Override
  generate_statement generate_statement() throws SemanticException;

  @Override
  generation_scheme generation_scheme() throws SemanticException;

  @Override
  generic_clause generic_clause() throws SemanticException;

  @Override
  generic_list generic_list() throws SemanticException;

  @Override
  generic_interface_list generic_interface_list() throws SemanticException;

  @Override
  group_constituent group_constituent() throws SemanticException;

  @Override
  group_constituent_list group_constituent_list() throws SemanticException;

  @Override
  group_template_declaration group_template_declaration() throws SemanticException;

  @Override
  group_declaration group_declaration() throws SemanticException;

  @Override
  guarded_signal_specification guarded_signal_specification() throws SemanticException;

  @Override
  if_statement if_statement() throws SemanticException;

  @Override
  index_constraint index_constraint() throws SemanticException;

  @Override
  index_specification index_specification() throws SemanticException;

  @Override
  indexed_name indexed_name() throws SemanticException;

  @Override
  instantiation_list instantiation_list() throws SemanticException;

  @Override
  interface_constant_declaration interface_constant_declaration() throws SemanticException;

  @Override
  interface_declaration interface_declaration() throws SemanticException;

  @Override
  interface_element interface_element() throws SemanticException;

  @Override
  interface_file_declaration interface_file_declaration() throws SemanticException;

  @Override
  interface_list interface_list() throws SemanticException;

  @Override
  interface_signal_declaration interface_signal_declaration() throws SemanticException;

  @Override
  interface_variable_declaration interface_variable_declaration() throws SemanticException;

  @Override
  iteration_scheme iteration_scheme() throws SemanticException;

  @Override
  literal literal() throws SemanticException;

  @Override
  loop_statement loop_statement() throws SemanticException;

  @Override
  miscellaneous_operator miscellaneous_operator() throws SemanticException;

  @Override
  mode mode() throws SemanticException;

  @Override
  next_statement next_statement() throws SemanticException;

  @Override
  numeric_literal numeric_literal() throws SemanticException;

  @Override
  object_declaration object_declaration() throws SemanticException;

  @Override
  operator_symbol operator_symbol() throws SemanticException;

  @Override
  options_ options_() throws SemanticException;

  @Override
  package_body package_body() throws SemanticException;

  @Override
  package_body_declarative_item package_body_declarative_item() throws SemanticException;

  @Override
  package_body_declarative_part package_body_declarative_part() throws SemanticException;

  @Override
  package_declaration package_declaration() throws SemanticException;

  @Override
  package_declarative_item package_declarative_item() throws SemanticException;

  @Override
  package_declarative_part package_declarative_part() throws SemanticException;

  @Override
  parameter_specification parameter_specification() throws SemanticException;

  @Override
  physical_literal physical_literal() throws SemanticException;

  @Override
  port_clause port_clause() throws SemanticException;

  @Override
  port_list port_list() throws SemanticException;

  @Override
  port_interface_list port_interface_list() throws SemanticException;

  @Override
  prefix prefix() throws SemanticException;

  @Override
  procedure_call procedure_call() throws SemanticException;

  @Override
  procedure_call_statement procedure_call_statement() throws SemanticException;

  @Override
  qualified_expression qualified_expression() throws SemanticException;

  @Override
  range range() throws SemanticException;

  @Override
  range_attribute_value range_attribute_value() throws SemanticException;

  @Override
  range_constraint range_constraint() throws SemanticException;

  @Override
  report_statement report_statement() throws SemanticException;

  @Override
  return_statement return_statement() throws SemanticException;

  @Override
  secondary_unit_declaration secondary_unit_declaration() throws SemanticException;

  @Override
  selected_signal_assignment selected_signal_assignment() throws SemanticException;

  @Override
  selected_waveforms selected_waveforms() throws SemanticException;

  @Override
  sensitivity_clause sensitivity_clause() throws SemanticException;

  @Override
  sequence_of_statements sequence_of_statements() throws SemanticException;

  @Override
  signal_assignment_statement signal_assignment_statement() throws SemanticException;

  @Override
  signal_declaration signal_declaration() throws SemanticException;

  @Override
  signal_kind signal_kind() throws SemanticException;

  @Override
  signal_list signal_list() throws SemanticException;

  @Override
  signature signature() throws SemanticException;

  @Override
  slice_name slice_name() throws SemanticException;

  @Override
  subprogram_body subprogram_body() throws SemanticException;

  @Override
  subprogram_declaration subprogram_declaration() throws SemanticException;

  @Override
  subprogram_declarative_item subprogram_declarative_item() throws SemanticException;

  @Override
  subprogram_declarative_part subprogram_declarative_part() throws SemanticException;

  @Override
  subprogram_kind subprogram_kind() throws SemanticException;

  @Override
  subprogram_specification subprogram_specification() throws SemanticException;

  @Override
  subprogram_statement_part subprogram_statement_part() throws SemanticException;

  @Override
  suffix suffix() throws SemanticException;

  @Override
  target target() throws SemanticException;

  @Override
  timeout_clause timeout_clause() throws SemanticException;

  @Override
  variable_assignment_statement variable_assignment_statement() throws SemanticException;

  @Override
  variable_declaration variable_declaration() throws SemanticException;

  @Override
  wait_statement wait_statement() throws SemanticException;

  @Override
  waveform waveform() throws SemanticException;

  @Override
  waveform_element waveform_element() throws SemanticException;

  @Override
  block_label block_label() throws SemanticException;

  @Override
  block_statement_label block_statement_label() throws SemanticException;

  @Override
  case_label case_label() throws SemanticException;

  @Override
  generate_label generate_label() throws SemanticException;

  @Override
  generate_statement_label generate_statement_label() throws SemanticException;

  @Override
  if_label if_label() throws SemanticException;

  @Override
  instantiation_label instantiation_label() throws SemanticException;

  @Override
  loop_label loop_label() throws SemanticException;

  @Override
  process_label process_label() throws SemanticException;

  @Override
  attribute_simple_name attribute_simple_name() throws SemanticException;

  @Override
  component_simple_name component_simple_name() throws SemanticException;

  @Override
  configuration_simple_name configuration_simple_name() throws SemanticException;

  @Override
  element_simple_name element_simple_name() throws SemanticException;

  @Override
  entity_simple_name entity_simple_name() throws SemanticException;

  @Override
  package_simple_name package_simple_name() throws SemanticException;

  @Override
  file_name file_name() throws SemanticException;

  @Override
  function_name function_name() throws SemanticException;

  @Override
  configuration_name configuration_name() throws SemanticException;

  @Override
  component_name component_name() throws SemanticException;

  @Override
  generic_name generic_name() throws SemanticException;

  @Override
  group_template_name group_template_name() throws SemanticException;

  @Override
  parameter_name parameter_name() throws SemanticException;

  @Override
  port_name port_name() throws SemanticException;

  @Override
  procedure_name procedure_name() throws SemanticException;

  @Override
  range_attribute_name range_attribute_name() throws SemanticException;

  @Override
  signal_name signal_name() throws SemanticException;

  @Override
  unit_name unit_name() throws SemanticException;

  @Override
  variable_name variable_name() throws SemanticException;

  @Override
  static_expression static_expression() throws SemanticException;

  @Override
  boolean_expression boolean_expression() throws SemanticException;

  @Override
  file_open_kind_expression file_open_kind_expression() throws SemanticException;

  @Override
  guard_expression guard_expression() throws SemanticException;

  @Override
  time_expression time_expression() throws SemanticException;

  @Override
  value_expression value_expression() throws SemanticException;

  @Override
  string_expression string_expression() throws SemanticException;

  @Override
  guarded_signal_list guarded_signal_list() throws SemanticException;

  @Override
  parameter_association_list parameter_association_list() throws SemanticException;

  @Override
  parameter_interface_list parameter_interface_list() throws SemanticException;

  @Override
  formal_port_clause formal_port_clause() throws SemanticException;

  @Override
  local_port_clause local_port_clause() throws SemanticException;

  @Override
  formal_generic_clause formal_generic_clause() throws SemanticException;

  @Override
  local_generic_clause local_generic_clause() throws SemanticException;

  @Override
  element_subtype_indication element_subtype_indication() throws SemanticException;

  @Override
  discrete_subtype_indication discrete_subtype_indication() throws SemanticException;

  @Override
  loop_parameter_specification loop_parameter_specification() throws SemanticException;

  @Override
  generate_parameter_specification generate_parameter_specification() throws SemanticException;

  @Override
  passive_concurrent_procedure_call_statement passive_concurrent_procedure_call_statement()
      throws SemanticException;

  @Override
  passive_process_statement passive_process_statement() throws SemanticException;

  @Override
  magnitude_simple_expression magnitude_simple_expression() throws SemanticException;

  @Override
  phase_simple_expression phase_simple_expression() throws SemanticException;

  public static final class Parsers {
    private Parsers() {
    }

    public static VHDL93ParserPartial forNode(SimpleNode node) {
      return forString(node.representation());
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
      public Object invoke(Object proxy, Method method, Object[] args)
          throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        try {
          method = VHDL93ParserTotal.class.getDeclaredMethod(method.getName(),
              method.getParameterTypes());
          Object result = method.invoke(parser, args);
          parser.eof();
          if (result instanceof Node) {
            ((Node) result).accept(new FlattenNestedListsVisitor());
          }
          return result;
        } catch (ParseException e) {
          throw new SyntaxException(e);
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
