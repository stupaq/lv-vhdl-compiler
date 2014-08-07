package stupaq.translation.parsing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import stupaq.vhdl93.ast.Node;
import stupaq.vhdl93.ast.NodeToken;
import stupaq.vhdl93.ast.identifier;
import stupaq.vhdl93.ast.label;
import stupaq.vhdl93.formatting.VHDLTreeFormatter;
import stupaq.vhdl93.visitor.TreeDumper;

import static stupaq.translation.parsing.VHDL93ParserPartial.Parsers.forString;

public class NodeRepr {
  private final String representation;

  private NodeRepr(String representation) {
    this.representation = representation;
  }

  public static NodeRepr repr(identifier n) {
    String rep = ((NodeToken) n.nodeChoice.choice).tokenImage;
    return rep == null ? null : repr(rep.toLowerCase().trim());
  }

  public static NodeRepr repr(label n) {
    return repr(n.identifier);
  }

  public static NodeRepr repr(Node node) {
    if (node instanceof identifier) {
      return repr((identifier) node);
    } else if (node instanceof label) {
      return repr((label) node);
    } else {
      node.accept(new VHDLTreeFormatter());
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        node.accept(new TreeDumper(baos));
        return repr(baos.toString().trim());
      } catch (IOException ignored) {
        return null;
      }
    }
  }

  public static NodeRepr repr(String repr) {
    return new NodeRepr(repr);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Node> T duplicate(T node) {
    try {
      return (T) VHDL93ParserPartial.class.getMethod(node.getClass().getSimpleName())
          .invoke(repr(node).as());
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException("Cannot find method for cloning.", e);
    }
  }

  public VHDL93ParserPartial as() {
    return forString(representation);
  }

  @Override
  public int hashCode() {
    return representation.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    return representation.equals(((NodeRepr) o).representation);
  }

  @Override
  public String toString() {
    return representation;
  }
}
