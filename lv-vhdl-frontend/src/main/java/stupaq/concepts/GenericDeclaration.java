package stupaq.concepts;

import stupaq.metadata.ConnectorPaneTerminal;
import stupaq.vhdl93.ast.interface_constant_declaration;

public class GenericDeclaration extends ConstantDeclaration implements ConnectorPaneTerminal {
  private int connectorIndex;

  public GenericDeclaration(interface_constant_declaration node) {
    super(node);
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
}
