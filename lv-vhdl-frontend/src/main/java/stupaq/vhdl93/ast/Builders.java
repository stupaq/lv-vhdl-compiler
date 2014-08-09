package stupaq.vhdl93.ast;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

import java.util.Arrays;

import stupaq.vhdl93.VHDL93ParserTotal;

public final class Builders {
  private Builders() {
  }

  public static NodeToken token(int kind) {
    return new NodeToken(VHDL93ParserTotal.tokenString(kind), kind, -1, -1, -1, -1);
  }

  public static NodeToken token(String image) {
    return new NodeToken(image);
  }

  public static Supplier<NodeToken> tokenSupplier(final int kind) {
    return new Supplier<NodeToken>() {
      @Override
      public NodeToken get() {
        return token(kind);
      }
    };
  }

  public static NodeSequence sequence(Node... args) {
    NodeSequence node = new NodeSequence(args.length);
    for (Node n : args) {
      node.addNode(n);
    }
    return node;
  }

  public static NodeOptional optional() {
    return new NodeOptional();
  }

  public static NodeOptional optional(Node node) {
    return new NodeOptional(node);
  }

  public static NodeChoice choice(Node node) {
    return new NodeChoice(node);
  }

  public static <T extends Node> NodeListOptional listOptional(Iterable<T> args) {
    NodeListOptional node = new NodeListOptional();
    for (Node n : args) {
      node.addNode(n);
    }
    return node;
  }

  public static NodeListOptional listOptional(Node... args) {
    return listOptional(Arrays.asList(args));
  }

  public static NodeList list(Node... args) {
    return list(Arrays.asList(args));
  }

  private static <T extends Node> NodeList list(Iterable<T> args) {
    NodeList node = new NodeList();
    for (Node n : args) {
      node.addNode(n);
    }
    return node;
  }

  public static <T extends Node> T split(Iterable<T> elements, Supplier<? extends Node> separator,
      NodeListOptional rest) {
    Preconditions.checkArgument(!Iterables.isEmpty(elements));
    T first = Iterables.get(elements, 0);
    for (T elem : Iterables.skip(elements, 1)) {
      rest.addNode(sequence(separator.get(), elem));
    }
    return first;
  }
}
