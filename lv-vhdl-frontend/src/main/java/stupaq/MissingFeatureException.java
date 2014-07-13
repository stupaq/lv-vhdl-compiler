package stupaq;

import stupaq.vhdl93.ast.SimpleNode;

public class MissingFeatureException extends LocalisedException {
  public MissingFeatureException(String message, SimpleNode near) {
    super(message, near);
  }

  public static void throwIf(boolean b, String message, SimpleNode n) {
    if (b) {
      throw new MissingFeatureException(message, n);
    }
  }
}
