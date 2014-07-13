package stupaq;

import stupaq.vhdl93.ast.SimpleNode;

public class SemanticException extends LocalisedException {
  protected SemanticException(SimpleNode near, String message, Object... args) {
    super(String.format(message, args), near);
  }

  public static <T> T checkNotNull(T x, SimpleNode n, String message, Object... args) {
    if (x == null) {
      throw new SemanticException(n, message, args);
    }
    return x;
  }

  public static void check(boolean b, SimpleNode n, String message, Object... args) {
    if (!b) {
      throw new SemanticException(n, message, args);
    }
  }
}
