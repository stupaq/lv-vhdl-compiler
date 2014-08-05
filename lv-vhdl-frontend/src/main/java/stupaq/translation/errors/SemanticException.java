package stupaq.translation.errors;

public class SemanticException extends TranslationException {
  public SemanticException(String message, Object... args) {
    super(String.format(message, args));
  }

  public static <T> T semanticNotNull(T x, String message, Object... args) {
    if (x == null) {
      throw new SemanticException(message, args);
    }
    return x;
  }

  public static void semanticCheck(boolean b, String message, Object... args) {
    if (!b) {
      throw new SemanticException(message, args);
    }
  }
}
