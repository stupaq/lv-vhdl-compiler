package stupaq;

import com.google.common.base.Optional;

import stupaq.vhdl93.ast.Position;
import stupaq.vhdl93.ast.SimpleNode;

public class MissingFeature extends RuntimeException {
  public MissingFeature(String message, Optional<Position> position) {
    super((position.isPresent() ? position.get().toString() : "") + message);
  }

  public static void throwIf(boolean b, String message, SimpleNode n) {
    if (b) {
      throw new MissingFeature(message, n.position());
    }
  }
}
