package stupaq.concepts;

import com.google.common.base.Preconditions;

import stupaq.vhdl93.ast.component_declaration;
import stupaq.vhdl93.ast.entity_declaration;
import stupaq.vhdl93.ast.entity_name;
import stupaq.vhdl93.ast.instantiated_unit;

public class ComponentName extends Identifier {
  private static final String DEFAULT_LIBRARY_PREFIX = "work.";
  private static final String LIBRARY_SEPARATOR = ".";

  public ComponentName(entity_declaration node) {
    super(entity(node.entity_identifier.identifier.representation()));
  }

  public ComponentName(component_declaration node) {
    super(component(node.component_identifier.identifier.representation()));
  }

  public ComponentName(entity_name node) {
    super(entity(node.name.representation()));
  }

  public ComponentName(instantiated_unit node) {
    super(node.firstName());
  }

  private static String component(String rep) {
    Preconditions.checkArgument(!rep.contains(LIBRARY_SEPARATOR));
    return rep;
  }

  private static String entity(String rep) {
    return rep.contains(LIBRARY_SEPARATOR) ? rep : DEFAULT_LIBRARY_PREFIX + rep;
  }
}
