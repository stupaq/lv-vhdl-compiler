package stupaq.vhdl93.extractors;

import com.google.common.base.Optional;

import stupaq.vhdl93.ast.Node;
import stupaq.vhdl93.ast.NodeToken;
import stupaq.vhdl93.ast.Position;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

public class PositionExtractorVisitor extends DepthFirstVisitor {
  private Optional<Position> position;

  public Optional<Position> extract(Node n) {
    position = Optional.absent();
    try {
      n.accept(this);
    } catch (VisitorBreakException ignored) {
    }
    return position;
  }

  @Override
  public void visit(NodeToken n) {
    position = Position.extract(n);
    if (position.isPresent()) {
      throw new VisitorBreakException();
    }
  }

  private class VisitorBreakException extends RuntimeException {
  }
}
