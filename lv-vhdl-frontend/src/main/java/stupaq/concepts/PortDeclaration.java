package stupaq.concepts;

import stupaq.MissingFeature;
import stupaq.vhdl93.VHDL93ParserConstants;
import stupaq.vhdl93.ast.NodeToken;
import stupaq.vhdl93.ast.interface_signal_declaration;
import stupaq.vhdl93.ast.mode;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

public class PortDeclaration extends SignalDeclaration {
  private PortDirection direction;

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

  public static enum PortDirection {
    IN,
    OUT
  }
}
