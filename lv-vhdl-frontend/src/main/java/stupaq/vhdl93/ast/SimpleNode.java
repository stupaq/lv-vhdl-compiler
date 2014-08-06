package stupaq.vhdl93.ast;

import com.google.common.base.Optional;

import stupaq.vhdl93.visitor.DepthFirstVisitor;

import static com.google.common.base.Optional.of;
import static stupaq.translation.parsing.NodeRepr.repr;

public abstract class SimpleNode implements Node {

  // FIXME remove this along with dependency on NodeRepr
  public String representation() {
    return repr(this).toString();
  }

  public Optional<Position> position() {
    return new PositionExtractorVisitor().extract(this);
  }

  public String firstName() {
    return new NameExtractorVisitor().extract(this).get();
  }

  private static class NameExtractorVisitor extends DepthFirstVisitor {
    private Optional<String> name;

    public Optional<String> extract(Node n) {
      name = Optional.absent();
      n.accept(this);
      return name;
    }

    @Override
    public void visit(name n) {
      name = name.or(of(n.representation()));
    }
  }

  private static class PositionExtractorVisitor extends DepthFirstVisitor {
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

    private static class VisitorBreakException extends RuntimeException {
    }
  }
}
