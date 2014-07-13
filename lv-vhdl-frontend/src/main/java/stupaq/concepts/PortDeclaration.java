package stupaq.concepts;

import com.google.common.base.Predicate;

import stupaq.MissingFeatureException;
import stupaq.vhdl93.ast.NodeToken;
import stupaq.vhdl93.ast.interface_signal_declaration;
import stupaq.vhdl93.ast.mode;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

import static stupaq.vhdl93.VHDL93ParserConstants.IN;
import static stupaq.vhdl93.VHDL93ParserConstants.OUT;
import static stupaq.vhdl93.VHDL93ParserConstants.tokenImage;

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
          case IN:
            direction = PortDirection.IN;
            break;
          case OUT:
            direction = PortDirection.OUT;
            break;
          default:
            throw new MissingFeatureException(n,
                "Port direction: " + tokenImage[mode] + " is not supported.");
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
