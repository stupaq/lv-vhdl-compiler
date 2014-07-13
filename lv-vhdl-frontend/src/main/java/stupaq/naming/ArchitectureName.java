package stupaq.naming;

import stupaq.lvproject.InstanceName;

public class ArchitectureName implements InstanceName {
  private final EntityName entity;
  private final Identifier arch;

  ArchitectureName(EntityName entity, Identifier arch) {
    this.entity = entity;
    this.arch = arch;
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
    return arch.equals(that.arch) && entity.equals(that.entity);
  }

  @Override
  public int hashCode() {
    int result = entity.hashCode();
    result = 31 * result + arch.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return entity + "(" + arch + ")";
  }

  @Override
  public String projectPathPart() {
    return toString();
  }

  @Override
  public InterfaceName interfaceName() {
    return entity;
  }

  public EntityName entity() {
    return entity;
  }
}
