package stupaq.vhdl93.ast;

import com.google.common.base.Optional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import stupaq.vhdl93.formatting.VHDLTreeFormatter;
import stupaq.vhdl93.visitor.DepthFirstVisitor;
import stupaq.vhdl93.visitor.TreeDumper;

import static com.google.common.base.Optional.of;

public abstract class SimpleNode implements Node {
  private static String representation(identifier n) {
    String rep = ((NodeToken) n.nodeChoice.choice).tokenImage;
    return rep == null ? null : rep.toLowerCase().trim();
  }

  private static String representation(label n) {
    return representation(n.identifier);
  }

  public String representation() {
    if (this instanceof identifier) {
      return representation((identifier) this);
    } else if (this instanceof label) {
      return representation((label) this);
    } else {
      this.accept(new VHDLTreeFormatter());
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        this.accept(new TreeDumper(baos));
        return baos.toString().trim();
      } catch (IOException ignored) {
        return null;
      }
    }
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
