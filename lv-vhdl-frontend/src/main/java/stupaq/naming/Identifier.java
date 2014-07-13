package stupaq.naming;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;

import stupaq.vhdl93.ast.entity_declaration;
import stupaq.vhdl93.ast.identifier;

public class Identifier {
  private final String string;

  public Identifier(identifier n) {
    this(n.representation());
  }

  protected Identifier(String string) {
    Preconditions.checkNotNull(string);
    string = string.trim().toLowerCase();
    Preconditions.checkArgument(!CharMatcher.WHITESPACE.matchesAnyOf(string));
    this.string = string;
  }

  @Override
  public int hashCode() {
    return string.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    return this == o ||
        !(o == null || getClass() != o.getClass()) && string.equals(((Identifier) o).string);
  }

  @Override
  public String toString() {
    return string;
  }

  public static EntityName entity(entity_declaration n) {
    return new EntityName()n.entity_identifier.identifier.representation();
    return rep.contains(LibraryName.LIBRARY_SEPARATOR) ? rep
        : LibraryName.DEFAULT_LIBRARY_PREFIX + rep;
  }
}
