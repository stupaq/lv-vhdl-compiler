package stupaq.vhdl93.visitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;

import stupaq.vhdl93.ast.*;

public class NonTerminalsNoOpVisitor<T> implements Visitor {
  private static final Logger LOGGER = LoggerFactory.getLogger(NonTerminalsNoOpVisitor.class);

  public T apply(Node n) {
    n.accept(this);
    return null;
  }

  private static void reportSkippedNode(Node n) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Skipping node: {}", n.getClass().getSimpleName());
    }
  }

  @Override
  public void visit(NodeList n) {
    for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      e.nextElement().accept(this);
    }
  }

  @Override
  public void visit(NodeListOptional n) {
    if (n.present()) {
      for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
        e.nextElement().accept(this);
      }
    }
  }

  @Override
  public void visit(NodeOptional n) {
    if (n.present()) {
      n.node.accept(this);
    }
  }

  @Override
  public void visit(NodeSequence n) {
    for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      e.nextElement().accept(this);
    }
  }

  @Override
  public void visit(NodeToken n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(eof n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(label n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(name_extension n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(identifier n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(identifier_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(association_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(named_association_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(positional_association_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(named_association_element n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(positional_association_element n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(formal_part n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(actual_part n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(actual_part_open n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(expression n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(constant_expression n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(logical_operator n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(relation_expression n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(constant_relation_expression n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(relational_operator n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(shift_expression n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(constant_shift_expression n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(shift_operator n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(simple_expression n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(constant_simple_expression n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(sign n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(adding_operator n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(term n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(constant_term n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(multiplying_operator n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(factor n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(constant_factor n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(primary n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(constant_primary n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(name_expression n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(access_type_definition n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(array_type_definition n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(composite_type_definition n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(element_subtype_definition n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(enumeration_type_definition n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(file_type_definition n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(floating_type_definition n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(full_type_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(incomplete_type_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(index_subtype_definition n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(integer_type_definition n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(physical_type_definition n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(physical_type_identifier n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(record_type_definition n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(record_type_identifier n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(scalar_type_definition n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(subtype_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(subtype_indication n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(resolution_function_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(type_conversion n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(type_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(type_definition n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(type_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(unconstrained_array_definition n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(design_file n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(design_unit n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(context_clause n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(context_item n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(library_clause n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(library_clause_item n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(use_clause n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(use_clause_item n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(library_unit n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(primary_unit n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(secondary_unit n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(entity_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(entity_header n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(entity_declarative_part n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(entity_declarative_item n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(entity_statement_part n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(entity_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(entity_identifier n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(entity_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(architecture_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(architecture_declarative_part n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(architecture_statement_part n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(architecture_identifier n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(architecture_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(concurrent_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(component_instantiation_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(instantiated_unit n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(generic_map_aspect n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(port_map_aspect n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(process_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(sensitivity_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(process_declarative_part n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(process_declarative_item n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(process_statement_part n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(sequential_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(null_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(abstract_literal n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(actual_parameter_part n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(aggregate n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(alias_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(alias_designator n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(allocator n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(assertion n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(assertion_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(attribute_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(attribute_designator n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(attribute_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(attribute_specification n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(base_unit_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(binding_indication n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(block_configuration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(block_declarative_item n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(block_declarative_part n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(block_header n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(block_specification n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(block_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(block_statement_part n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(case_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(case_statement_alternative n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(choice n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(choices n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(component_configuration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(component_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(component_identifier n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(component_header n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(component_specification n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(concurrent_assertion_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(concurrent_procedure_call_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(concurrent_signal_assignment_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(condition n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(condition_clause n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(conditional_signal_assignment n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(conditional_waveforms n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(configuration_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(configuration_declarative_item n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(configuration_declarative_part n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(configuration_item n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(configuration_specification n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(constant_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(constrained_array_definition n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(constraint n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(delay_mechanism n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(designator n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(direction n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(disconnection_specification n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(discrete_range n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(element_association n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(element_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(entity_aspect n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(shared_variable_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(entity_class n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(entity_class_entry n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(entity_class_entry_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(entity_designator n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(entity_name_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(entity_specification n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(entity_tag n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(enumeration_literal n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(exit_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(file_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(file_logical_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(file_open_information n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(formal_parameter_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(function_call n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(generate_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(generation_scheme n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(generic_clause n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(generic_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(generic_interface_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(group_constituent n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(group_constituent_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(group_template_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(group_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(guarded_signal_specification n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(if_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(index_constraint n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(index_specification n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(indexed_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(instantiation_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(interface_constant_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(interface_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(interface_element n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(interface_file_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(interface_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(interface_signal_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(interface_variable_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(iteration_scheme n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(literal n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(loop_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(miscellaneous_operator n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(mode n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(next_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(numeric_literal n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(object_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(operator_symbol n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(options_ n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(package_body n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(package_body_declarative_item n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(package_body_declarative_part n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(package_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(package_declarative_item n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(package_declarative_part n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(parameter_specification n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(physical_literal n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(port_clause n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(port_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(port_interface_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(prefix n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(procedure_call n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(procedure_call_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(qualified_expression n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(range n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(range_attribute_value n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(range_constraint n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(report_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(return_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(secondary_unit_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(selected_signal_assignment n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(selected_waveforms n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(sensitivity_clause n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(sequence_of_statements n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(signal_assignment_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(signal_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(signal_kind n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(signal_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(signature n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(slice_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(subprogram_body n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(subprogram_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(subprogram_declarative_item n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(subprogram_declarative_part n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(subprogram_kind n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(subprogram_specification n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(subprogram_statement_part n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(suffix n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(target n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(timeout_clause n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(variable_assignment_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(variable_declaration n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(wait_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(waveform n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(waveform_element n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(block_label n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(block_statement_label n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(case_label n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(generate_label n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(generate_statement_label n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(if_label n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(instantiation_label n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(loop_label n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(process_label n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(attribute_simple_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(component_simple_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(configuration_simple_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(element_simple_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(entity_simple_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(package_simple_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(file_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(function_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(configuration_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(component_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(generic_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(group_template_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(parameter_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(port_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(procedure_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(range_attribute_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(signal_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(unit_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(variable_name n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(static_expression n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(boolean_expression n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(file_open_kind_expression n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(guard_expression n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(time_expression n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(value_expression n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(string_expression n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(guarded_signal_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(parameter_association_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(parameter_interface_list n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(formal_port_clause n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(local_port_clause n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(formal_generic_clause n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(local_generic_clause n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(element_subtype_indication n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(discrete_subtype_indication n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(loop_parameter_specification n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(generate_parameter_specification n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(passive_concurrent_procedure_call_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(passive_process_statement n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(magnitude_simple_expression n) {
    reportSkippedNode(n);
  }

  @Override
  public void visit(phase_simple_expression n) {
    reportSkippedNode(n);
  }
}
