package stupaq.vhdl93.visitor;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Vector;

import stupaq.vhdl93.ast.*;

public abstract class UserDefinedTreeFormatter extends TreeFormatter {
  protected final Vector<FormatCommand> cmdQueue;

  @SuppressWarnings("unchecked")
  public UserDefinedTreeFormatter(int indentAmt, int wrapWidth) {
    super(indentAmt, wrapWidth);
    try {
      Field formatterQueue = TreeFormatter.class.getDeclaredField("cmdQueue");
      formatterQueue.setAccessible(true);
      cmdQueue = (Vector<FormatCommand>) formatterQueue.get(this);
      formatterQueue.setAccessible(false);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected final void processList(FormatCommand pre, NodeListInterface n, FormatCommand post) {
    boolean first = true;
    for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      if (pre != null && !first) {
        add(pre);
      }
      first = false;
      e.nextElement().accept(this);
      if (post != null && e.hasMoreElements()) {
        add(post);
      }
    }
  }

  protected final void processOptionalList(FormatCommand pre, NodeListOptional list,
      FormatCommand post) {
    if (list.present()) {
      processList(pre, list, post);
    }
  }

  @Override
  public final void visit(abstract_literal n) {
    super.visit(n);
  }

  @Override
  public final void visit(actual_designator n) {
    super.visit(n);
  }

  @Override
  public final void visit(actual_parameter_part n) {
    super.visit(n);
  }

  @Override
  public final void visit(actual_part n) {
    super.visit(n);
  }

  @Override
  public final void visit(adding_operator n) {
    super.visit(n);
  }

  @Override
  public final void visit(alias_designator n) {
    super.visit(n);
  }

  @Override
  public final void visit(architecture_statement n) {
    super.visit(n);
  }

  @Override
  public final void visit(array_type_definition n) {
    super.visit(n);
  }

  @Override
  public final void visit(block_declarative_item n) {
    super.visit(n);
  }

  @Override
  public final void visit(block_specification n) {
    super.visit(n);
  }

  @Override
  public final void visit(choice n) {
    super.visit(n);
  }

  @Override
  public final void visit(composite_type_definition n) {
    super.visit(n);
  }

  @Override
  public final void visit(configuration_declarative_item n) {
    super.visit(n);
  }

  @Override
  public final void visit(configuration_item n) {
    super.visit(n);
  }

  @Override
  public final void visit(constraint n) {
    super.visit(n);
  }

  @Override
  public final void visit(context_item n) {
    super.visit(n);
  }

  @Override
  public final void visit(delay_mechanism n) {
    super.visit(n);
  }

  @Override
  public final void visit(designator n) {
    super.visit(n);
  }

  @Override
  public final void visit(direction n) {
    super.visit(n);
  }

  @Override
  public final void visit(discrete_range n) {
    super.visit(n);
  }

  @Override
  public final void visit(entity_aspect n) {
    super.visit(n);
  }

  @Override
  public final void visit(entity_class n) {
    super.visit(n);
  }

  @Override
  public final void visit(entity_declarative_item n) {
    super.visit(n);
  }

  @Override
  public final void visit(entity_name_list n) {
    super.visit(n);
  }

  @Override
  public final void visit(entity_statement n) {
    super.visit(n);
  }

  @Override
  public final void visit(entity_tag n) {
    super.visit(n);
  }

  @Override
  public final void visit(enumeration_literal n) {
    super.visit(n);
  }

  @Override
  public final void visit(factor n) {
    super.visit(n);
  }

  @Override
  public final void visit(formal_designator n) {
    super.visit(n);
  }

  @Override
  public final void visit(formal_part n) {
    super.visit(n);
  }

  @Override
  public final void visit(generation_scheme n) {
    super.visit(n);
  }

  @Override
  public final void visit(group_constituent n) {
    super.visit(n);
  }

  @Override
  public final void visit(identifier n) {
    super.visit(n);
  }

  @Override
  public final void visit(index_specification n) {
    super.visit(n);
  }

  @Override
  public final void visit(instantiated_unit n) {
    super.visit(n);
  }

  @Override
  public final void visit(instantiation_list n) {
    super.visit(n);
  }

  @Override
  public final void visit(interface_declaration n) {
    super.visit(n);
  }

  @Override
  public final void visit(iteration_scheme n) {
    super.visit(n);
  }

  @Override
  public final void visit(library_unit n) {
    super.visit(n);
  }

  @Override
  public final void visit(literal n) {
    super.visit(n);
  }

  @Override
  public final void visit(logical_operator n) {
    super.visit(n);
  }

  @Override
  public final void visit(miscellaneous_operator n) {
    super.visit(n);
  }

  @Override
  public final void visit(mode n) {
    super.visit(n);
  }

  @Override
  public final void visit(multiplying_operator n) {
    super.visit(n);
  }

  @Override
  public final void visit(numeric_literal n) {
    super.visit(n);
  }

  @Override
  public final void visit(object_declaration n) {
    super.visit(n);
  }

  @Override
  public final void visit(package_declarative_item n) {
    super.visit(n);
  }

  @Override
  public final void visit(prefix n) {
    super.visit(n);
  }

  @Override
  public final void visit(primary n) {
    super.visit(n);
  }

  @Override
  public final void visit(primary_unit n) {
    super.visit(n);
  }

  @Override
  public final void visit(process_declarative_item n) {
    super.visit(n);
  }

  @Override
  public final void visit(range n) {
    super.visit(n);
  }

  @Override
  public final void visit(relational_operator n) {
    super.visit(n);
  }

  @Override
  public final void visit(secondary_unit n) {
    super.visit(n);
  }

  @Override
  public final void visit(shift_operator n) {
    super.visit(n);
  }

  @Override
  public final void visit(sign n) {
    super.visit(n);
  }

  @Override
  public final void visit(signal_kind n) {
    super.visit(n);
  }

  @Override
  public final void visit(signal_list n) {
    super.visit(n);
  }

  @Override
  public final void visit(subprogram_declarative_item n) {
    super.visit(n);
  }

  @Override
  public final void visit(subprogram_kind n) {
    super.visit(n);
  }

  @Override
  public final void visit(subprogram_specification n) {
    super.visit(n);
  }

  @Override
  public final void visit(subtype_indication n) {
    super.visit(n);
  }

  @Override
  public final void visit(suffix n) {
    super.visit(n);
  }

  @Override
  public final void visit(target n) {
    super.visit(n);
  }

  @Override
  public final void visit(type_declaration n) {
    super.visit(n);
  }

  @Override
  public final void visit(type_definition n) {
    super.visit(n);
  }

  @Override
  public final void visit(type_mark n) {
    super.visit(n);
  }

  @Override
  public final void visit(waveform n) {
    super.visit(n);
  }

  @Override
  public final void visit(waveform_element n) {
    super.visit(n);
  }

  @Override
  public final void visit(attribute_simple_name n) {
    super.visit(n);
  }
}
