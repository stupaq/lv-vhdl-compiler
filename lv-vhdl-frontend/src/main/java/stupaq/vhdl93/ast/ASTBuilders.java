package stupaq.vhdl93.ast;

public final class ASTBuilders {
  private ASTBuilders() {
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

  public static NodeListOptional listOptional() {
    return new NodeListOptional();
  }
}
