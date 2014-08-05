package stupaq.translation.errors;

import stupaq.vhdl93.ast.SimpleNode;

public class MissingFeatureException extends TranslationException {
  public MissingFeatureException(SimpleNode near, String message, Object... args) {
    super(String.format(message, args));
  }

  public MissingFeatureException(String message, Object... args) {
    super(String.format(message, args));
  }

  public static void missingIf(boolean b, SimpleNode n, String message, Object... args) {
    if (b) {
      throw new MissingFeatureException(n, message, args);
    }
  }

  public static void missingIf(boolean b, String message, Object... args) {
    if (b) {
      throw new MissingFeatureException(message, args);
    }
  }
}
