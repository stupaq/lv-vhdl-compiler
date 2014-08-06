package stupaq.translation.parsing;

import stupaq.vhdl93.ast.SimpleNode;

import static stupaq.translation.parsing.VHDL93ParserPartial.Parsers.forString;

public class AnyNode {
  private final String representation;

  private AnyNode(String representation) {
    this.representation = representation;
  }

  public AnyNode(SimpleNode node) {
    this(node.representation());
  }

  public VHDL93ParserPartial as() {
    return forString(representation);
  }
}
