package stupaq.vhdl93.ast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import stupaq.vhdl93.visitor.GJNoArguDepthFirst;
import stupaq.vhdl93.visitor.TreeDumper;
import stupaq.vhdl93.visitor.VHDLTreeFormatter;

import static stupaq.vhdl93.ast.ASTBuilders.sequence;

public final class ASTGetters {
  private ASTGetters() {
  }

  public static String name(SimpleNode n) {
    String name = sequence(n).accept(new GJNoArguDepthFirst<String>() {
      String name;

      @Override
      public String visit(NodeSequence n) {
        super.visit(n);
        return name;
      }

      @Override
      public String visit(name n) {
        return (name = representation(n));
      }
    });
    return name == null ? null : name.trim();
  }

  public static String representation(SimpleNode n) {
    n.accept(new VHDLTreeFormatter());
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      n.accept(new TreeDumper(baos));
      return baos.toString().trim();
    } catch (IOException ignored) {
      return null;
    }
  }

  public static String representation(identifier n) {
    String rep = n.nodeChoice.accept(new GJNoArguDepthFirst<String>() {
      @Override
      public String visit(NodeToken n) {
        return n.tokenImage.toLowerCase();
      }
    });
    return rep == null ? null : rep.trim();
  }

  public static String representation(label n) {
    return representation(n.identifier);
  }
}
