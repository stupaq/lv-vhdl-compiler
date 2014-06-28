package stupaq.vhdl93.ast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import stupaq.vhdl93.visitor.GJNoArguDepthFirst;
import stupaq.vhdl93.visitor.TreeDumper;
import stupaq.vhdl93.visitor.VHDLTreeFormatter;

public class ASTGetters {
  public static String representation(SimpleNode n) {
    n.accept(new VHDLTreeFormatter());
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      n.accept(new TreeDumper(baos));
      return baos.toString();
    } catch (IOException ignored) {
      return null;
    }
  }

  public static String representation(identifier n) {
    return n.nodeChoice.accept(new GJNoArguDepthFirst<String>() {
      @Override
      public String visit(NodeToken n) {
        return n.tokenImage.toLowerCase();
      }
    });
  }

  public static String representation(label n) {
    return representation(n.identifier);
  }
}
