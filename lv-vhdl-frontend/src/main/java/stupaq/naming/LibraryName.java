package stupaq.naming;

public class LibraryName extends Identifier {
  static final LibraryName DEFAULT_LIBRARY = new LibraryName("work");
  static final char LIBRARY_SEPARATOR = '.';

  private LibraryName(String string) {
    super(string);
  }
}
