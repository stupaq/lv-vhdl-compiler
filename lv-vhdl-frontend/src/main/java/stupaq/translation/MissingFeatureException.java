package stupaq.translation;

import com.google.common.base.Optional;

import stupaq.vhdl93.ast.Position;
import stupaq.vhdl93.ast.SimpleNode;

public class MissingFeatureException extends AbstractLocalisedException {
  public MissingFeatureException(SimpleNode near, String message, Object... args) {
    super(String.format(message, args), near);
  }

  public MissingFeatureException(String message, Object... args) {
    super(String.format(message, args), Optional.<Position>absent());
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
