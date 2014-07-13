package stupaq.naming;

import stupaq.vhdl93.ast.identifier;

public class ArchitectureName {
  private final EntityName name;
  private final Identifier arch;

  public ArchitectureName(EntityName name, identifier arch) {
    this.name = name;
    this.arch = new Identifier(arch);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArchitectureName that = (ArchitectureName) o;
    return arch.equals(that.arch) && name.equals(that.name);
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + arch.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return name + "(" + arch + ")";
  }
}
