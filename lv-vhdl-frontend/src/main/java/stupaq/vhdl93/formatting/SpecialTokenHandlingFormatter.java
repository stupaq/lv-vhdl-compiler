package stupaq.vhdl93.formatting;

import java.util.Vector;

import stupaq.vhdl93.VHDL93ParserConstants;
import stupaq.vhdl93.ast.NodeToken;

public class SpecialTokenHandlingFormatter extends LineBreakingTreeFormatter
    implements VHDL93ParserConstants {
  public SpecialTokenHandlingFormatter(int indentAmt, int wrapWidth) {
    super(indentAmt, wrapWidth);
  }

  @Override
  public void visit(NodeToken n) {
    // Comment is the only special token we have.
    Vector<NodeToken> specialTokens = n.specialTokens;
    if (specialTokens != null) {
      for (NodeToken special : n.specialTokens) {
        super.visit(special);
        add(force());
      }
    }
    n.specialTokens = null;
    super.visit(n);
    n.specialTokens = specialTokens;
  }
}
