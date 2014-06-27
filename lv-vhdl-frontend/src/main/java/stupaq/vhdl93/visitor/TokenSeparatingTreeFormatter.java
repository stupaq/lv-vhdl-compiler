package stupaq.vhdl93.visitor;

import com.google.common.collect.Sets;

import java.util.Set;

import stupaq.vhdl93.ast.NodeToken;
import stupaq.vhdl93.ast.SimpleNode;

public class TokenSeparatingTreeFormatter extends UserDefinedTreeFormatter {
  private final static Set<String> NO_PRE_SPACES = Sets.newHashSet(",", ";", ".", "(", ")");
  private final static Set<String> NO_POST_SPACES = Sets.newHashSet("", ".", "(", ")");
  private boolean autoSurroundTokens = true;
  private String lastToken = "";

  public TokenSeparatingTreeFormatter(int indentAmt, int wrapWidth) {
    super(indentAmt, wrapWidth);
  }

  protected final void setTokenSeparationFor(SimpleNode node, boolean value) {
    autoSurroundTokens ^= value;
    value ^= autoSurroundTokens;
    node.accept(this);
    autoSurroundTokens = value;
  }

  protected final void stripSpaces() {
    for (int i = cmdQueue.size() - 1; i >= 0; --i) {
      switch (cmdQueue.get(i).getCommand()) {
        case FormatCommand.SPACE:
          cmdQueue.remove(i);
        case FormatCommand.INDENT:
        case FormatCommand.OUTDENT:
        case FormatCommand.FORCE:
          break;
        default:
          i = -1;
      }
    }
  }

  protected final void ensureWhiteSpace() {
    for (int i = cmdQueue.size() - 1; i >= 0; --i) {
      switch (cmdQueue.get(i).getCommand()) {
        case FormatCommand.INDENT:
        case FormatCommand.OUTDENT:
          break;
        case FormatCommand.SPACE:
        case FormatCommand.FORCE:
          return;
        default:
          i = -1;
      }
    }
    add(space());
  }

  @Override
  public void visit(NodeToken n) {
    if (autoSurroundTokens) {
      if (!NO_PRE_SPACES.contains(n.tokenImage) && !NO_POST_SPACES.contains(lastToken)) {
        ensureWhiteSpace();
      }
    }
    lastToken = n.tokenImage;
    super.visit(n);
  }
}
