package stupaq.naming;

public class ComponentName implements InstantiableName, InterfaceName {
  private final ArchitectureName arch;
  private final Identifier component;

  ComponentName(ArchitectureName arch, Identifier component) {
    this.arch = arch;
    this.component = component;
  }

  @Override
  public int hashCode() {
    int result = arch.hashCode();
    result = 31 * result + component.hashCode();
    return result;
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
  public String toString() {
    return arch + String.valueOf(LibraryName.LIBRARY_SEPARATOR) + component;
  }

  @Override
  public String elementName() {
    return toString();
  }

  @Override
  public InterfaceName interfaceName() {
    return this;
  }

  public Identifier component() {
    return component;
  }
}
