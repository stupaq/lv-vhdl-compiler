package stupaq.translation.naming;

public class LibraryName extends Identifier {
  public static final LibraryName DEFAULT_LIBRARY = new LibraryName("work");
  public static final char LIBRARY_SEPARATOR = '.';

  private LibraryName(String string) {
    super(string);
  }
}
