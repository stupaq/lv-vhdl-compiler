package stupaq;

import stupaq.vhdl93.ast.SimpleNode;

public class MissingFeatureException extends LocalisedException {
  public MissingFeatureException(SimpleNode near, String message, Object... args) {
    super(String.format(message, args), near);
  }

  public static void throwIf(boolean b, SimpleNode n, String message, Object... args) {
    if (b) {
      throw new MissingFeatureException(n, message, args);
    }
  }
}
