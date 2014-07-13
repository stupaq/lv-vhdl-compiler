package stupaq.vhdl93.ast;

import com.google.common.base.Optional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import stupaq.vhdl93.visitor.NameExtractorVisitor;
import stupaq.vhdl93.visitor.PositionExtractorVisitor;
import stupaq.vhdl93.visitor.TreeDumper;
import stupaq.vhdl93.visitor.VHDLTreeFormatter;

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
}
