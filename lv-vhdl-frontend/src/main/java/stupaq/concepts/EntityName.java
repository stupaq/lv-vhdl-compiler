package stupaq.concepts;

import stupaq.vhdl93.ast.entity_declaration;
import stupaq.vhdl93.ast.entity_name;
import stupaq.vhdl93.ast.instantiated_unit;

public class EntityName extends Identifier {
  private static final String DEFAULT_LIBRARY_PREFIX = "work.";
  private static final String LIBRARY_SEPARATOR = ".";

  public EntityName(String name) {
    super(name.contains(LIBRARY_SEPARATOR) ? name : DEFAULT_LIBRARY_PREFIX + name);
  }

  public EntityName(entity_declaration node) {
    this(node.entity_identifier.identifier.representation());
  }

  public EntityName(entity_name node) {
    this(node.name.representation());
  }

  public EntityName(instantiated_unit node) {
    this(node.firstName());
  }
}
