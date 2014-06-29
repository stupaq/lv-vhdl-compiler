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
}
