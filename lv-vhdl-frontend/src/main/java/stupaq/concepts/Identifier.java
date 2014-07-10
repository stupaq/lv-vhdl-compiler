package stupaq.concepts;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;

import stupaq.vhdl93.ast.identifier;

public class Identifier {
  private final String string;

  public Identifier(String string) {
    Preconditions.checkNotNull(string);
    string = string.trim().toLowerCase();
    Preconditions.checkArgument(!CharMatcher.WHITESPACE.matchesAnyOf(string));
    this.string = string;
  }

  public Identifier(identifier node) {
    this(node.representation());
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
}
