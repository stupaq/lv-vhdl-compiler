package stupaq.translation;

import com.google.common.base.Optional;

import stupaq.vhdl93.ast.Position;
import stupaq.vhdl93.ast.SimpleNode;

abstract class AbstractLocalisedException extends RuntimeException {
  protected AbstractLocalisedException(String message, Optional<Position> position) {
    super((position.isPresent() ? position.get().toString() + " | " : "") + message);
  }

  protected AbstractLocalisedException(String message, SimpleNode near) {
    this(message, near.position());
  }
}
