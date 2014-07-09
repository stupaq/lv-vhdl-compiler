package stupaq.concepts;

import com.google.common.base.Predicate;

import stupaq.MissingFeature;
import stupaq.metadata.ConnectorPaneTerminal;
import stupaq.vhdl93.VHDL93ParserConstants;
import stupaq.vhdl93.ast.NodeToken;
import stupaq.vhdl93.ast.interface_signal_declaration;
import stupaq.vhdl93.ast.mode;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

public class PortDeclaration extends SignalDeclaration implements ConnectorPaneTerminal {
  private PortDirection direction;
  private int connectorIndex;

  public PortDeclaration(interface_signal_declaration node) {
    super(node);
    direction = PortDirection.IN;
    node.accept(new DepthFirstVisitor() {
      public void visit(mode n) {
        int mode = ((NodeToken) n.nodeChoice.choice).kind;
        switch (mode) {
          case VHDL93ParserConstants.IN:
            direction = PortDirection.IN;
            break;
          case VHDL93ParserConstants.OUT:
            direction = PortDirection.OUT;
            break;
          default:
            throw new MissingFeature("Mode: " + VHDL93ParserConstants.tokenImage[mode] +
                " is not supported for ports.");
        }
      }
    });
  }

  public PortDirection direction() {
    return direction;
  }

  @Override
  public boolean isInput() {
    return direction == PortDirection.IN;
  }

  @Override
  public boolean isConstant() {
    return false;
  }

  @Override
  public int connectorIndex() {
    return connectorIndex;
  }

  @Override
  public void connectorIndex(int index) {
    this.connectorIndex = index;
  }

  public static enum PortDirection {
    IN,
    OUT
  }

  public static class DirectionPredicate implements Predicate<PortDeclaration> {
    private final PortDirection direction;

    public DirectionPredicate(PortDirection direction) {
      this.direction = direction;
    }

    @Override
    public boolean apply(PortDeclaration input) {
      return direction == input.direction();
    }
  }
}
