package stupaq;

import com.google.common.base.Optional;

import stupaq.vhdl93.ast.Position;
import stupaq.vhdl93.ast.SimpleNode;

public class SemanticException extends LocalisedException {
  public SemanticException(String message) {
    super(message, Optional.<Position>absent());
  }

  protected SemanticException(String message, SimpleNode near) {
    super(message, near);
  }

  public static <T> T checkNotNull(T x, SimpleNode n, String message, Object... args) {
    if (x == null) {
      throw new SemanticException(String.format(message, args), n);
    }
    return x;
  }

  public static void check(boolean b, SimpleNode n, String message, Object... args) {
    if (b) {
      throw new SemanticException(String.format(message, args), n);
    }
  }
}
