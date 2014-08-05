package stupaq.translation.errors;

import stupaq.vhdl93.ast.SimpleNode;

public class LocalisedSemanticException extends SemanticException {
  public LocalisedSemanticException(SimpleNode near, String message, Object... args) {
    super(message, args);
  }

  public LocalisedSemanticException(String message, Object... args) {
    super(message, args);
  }

  public static <T> T semanticNotNull(T x, SimpleNode n, String message, Object... args) {
    if (x == null) {
      throw new LocalisedSemanticException(n, message, args);
    }
    return x;
  }

  public static void semanticCheck(boolean b, SimpleNode n, String message, Object... args) {
    if (!b) {
      throw new LocalisedSemanticException(n, message, args);
    }
  }
}
