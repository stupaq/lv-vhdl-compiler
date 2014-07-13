package stupaq.naming;

import stupaq.vhdl93.ast.identifier;

public class ComponentName {
  private final ArchitectureName arch;
  private final Identifier component;

  public ComponentName(ArchitectureName arch, identifier component) {
    this.arch = arch;
    this.component = new Identifier(component);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ComponentName that = (ComponentName) o;
    return arch.equals(that.arch) && component.equals(that.component);
  }

  @Override
  public int hashCode() {
    int result = arch.hashCode();
    result = 31 * result + component.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return arch + LibraryName.LIBRARY_SEPARATOR + component;
  }

  public Identifier local() {
    return component;
  }
}
