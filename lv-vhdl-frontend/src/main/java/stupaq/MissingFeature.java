package stupaq;

import com.google.common.base.Optional;

import stupaq.vhdl93.ast.Position;
import stupaq.vhdl93.ast.SimpleNode;

public class MissingFeature extends RuntimeException {
  private MissingFeature(String message, Optional<Position> position) {
    super((position.isPresent() ? position.get().toString() : "") + message);
  }

  public static void missing(String message, SimpleNode n) {
    throw new MissingFeature(message, n.position());
  }

  public static void missingIf(boolean b, String message, SimpleNode n) {
    if (b) {
      missing(message, n);
    }
  }
}
