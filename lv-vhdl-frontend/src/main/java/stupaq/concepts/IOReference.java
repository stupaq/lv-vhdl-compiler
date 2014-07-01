package stupaq.concepts;

import com.google.common.base.CharMatcher;

import stupaq.vhdl93.ast.identifier;

import static stupaq.vhdl93.ast.ASTGetters.representation;

public class IOReference {
  private final String name;

  public IOReference(String name) {
    this.name = CharMatcher.WHITESPACE.trimFrom(name);
  }

  public IOReference(identifier n) {
    this(representation(n));
  }

  @Override
  public boolean equals(Object o) {
    return this == o ||
        !(o == null || getClass() != o.getClass()) && name.equals(((IOReference) o).name);

  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
