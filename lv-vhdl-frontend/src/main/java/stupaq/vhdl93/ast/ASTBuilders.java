package stupaq.vhdl93.ast;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

import java.util.Arrays;

import stupaq.vhdl93.VHDL93Parser;

public final class ASTBuilders {
  private ASTBuilders() {
  }

  public static NodeToken token(int kind) {
    return new NodeToken(VHDL93Parser.tokenString(kind), kind, -1, -1, -1, -1);
  }

  public static Supplier<NodeToken> tokenSupplier(final int kind) {
    return new Supplier<NodeToken>() {
      @Override
      public NodeToken get() {
        return token(kind);
      }
    };
  }

  public static NodeSequence sequence(SimpleNode... args) {
    NodeSequence node = new NodeSequence(args.length);
    for (SimpleNode n : args) {
      node.addNode(n);
    }
    return node;
  }

  public static NodeOptional optional() {
    return new NodeOptional();
  }

  public static NodeOptional optional(SimpleNode node) {
    return new NodeOptional(node);
  }

  public static NodeChoice choice(SimpleNode node) {
    return new NodeChoice(node);
  }

  public static <T extends SimpleNode> NodeListOptional listOptional(Iterable<T> args) {
    NodeListOptional node = new NodeListOptional();
    for (SimpleNode n : args) {
      node.addNode(n);
    }
    return node;
  }

  public static NodeListOptional listOptional(SimpleNode... args) {
    return listOptional(Arrays.asList(args));
  }

  public static NodeList list(SimpleNode... args) {
    return list(Arrays.asList(args));
  }

  private static <T extends SimpleNode> NodeList list(Iterable<T> args) {
    NodeList node = new NodeList();
    for (SimpleNode n : args) {
      node.addNode(n);
    }
    return node;
  }

  public static <T extends SimpleNode> T split(Iterable<T> elements,
      Supplier<? extends SimpleNode> separator, NodeListOptional rest) {
    Preconditions.checkArgument(!Iterables.isEmpty(elements));
    T first = Iterables.get(elements, 0);
    for (T elem : Iterables.skip(elements, 1)) {
      rest.addNode(sequence(separator.get(), elem));
    }
    return first;
  }
}
