package stupaq.translation.vhdl2lv;

import stupaq.vhdl93.ast.interface_constant_declaration;

import static stupaq.vhdl93.VHDL93ParserConstants.CONSTANT;
import static stupaq.vhdl93.ast.Builders.optional;
import static stupaq.vhdl93.ast.Builders.token;

class GenericDeclaration extends ConstantDeclaration implements ConnectorPaneTerminal {
  private final interface_constant_declaration node;
  private int connectorIndex;

  public GenericDeclaration(interface_constant_declaration node) {
    super(node);
    this.node = node;
    node.nodeOptional = optional(token(CONSTANT));
  }

  @Override
  public boolean isInput() {
    return true;
  }

  @Override
  public boolean isConstant() {
    return true;
  }

  @Override
  public int connectorIndex() {
    return connectorIndex;
  }

  @Override
  public void connectorIndex(int index) {
    this.connectorIndex = index;
  }

  @Override
  public String representation() {
    return node.representation();
  }
}
