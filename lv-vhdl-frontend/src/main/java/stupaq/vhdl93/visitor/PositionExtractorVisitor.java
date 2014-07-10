package stupaq.vhdl93.visitor;

import com.google.common.base.Optional;

import stupaq.vhdl93.ast.Node;
import stupaq.vhdl93.ast.NodeToken;
import stupaq.vhdl93.ast.Position;

public class PositionExtractorVisitor extends DepthFirstVisitor {
  private Optional<Position> position;

  public Optional<Position> extract(Node n) {
    position = Optional.absent();
    n.accept(this);
    return position;
  }

  @Override
  public void visit(NodeToken n) {
    position = position.or(Position.extract(n));
  }
}
