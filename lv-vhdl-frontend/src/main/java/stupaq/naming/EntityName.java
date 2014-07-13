package stupaq.naming;

public class EntityName implements InterfaceName {
  private final LibraryName library;
  private final Identifier entity;

  EntityName(LibraryName library, Identifier entity) {
    this.library = library;
    this.entity = entity;
  }

  @Override
  public String toString() {
    return library + String.valueOf(LibraryName.LIBRARY_SEPARATOR) + entity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EntityName that = (EntityName) o;
    return entity.equals(that.entity) && library.equals(that.library);
  }

  @Override
  public int hashCode() {
    int result = library.hashCode();
    result = 31 * result + entity.hashCode();
    return result;
  }
}
