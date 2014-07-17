package stupaq;

import com.google.common.base.Optional;

import stupaq.vhdl93.ast.Position;
import stupaq.vhdl93.ast.SimpleNode;

public class SemanticException extends AbstractLocalisedException {
  protected SemanticException(SimpleNode near, String message, Object... args) {
    super(String.format(message, args), near);
  }

  protected SemanticException(String message, Object... args) {
    super(String.format(message, args), Optional.<Position>absent());
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

  public static void check(boolean b, String message, Object... args) {
    if (!b) {
      throw new SemanticException(message, args);
    }
  }
}
