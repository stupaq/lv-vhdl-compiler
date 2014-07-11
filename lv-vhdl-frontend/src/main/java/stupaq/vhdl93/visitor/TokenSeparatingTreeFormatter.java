package stupaq.vhdl93.visitor;

import stupaq.formatting.TokenMatchingActionExecutor;
import stupaq.formatting.TokenMatchingActionExecutor.Action;
import stupaq.formatting.TokenMatchingActionExecutor.TokenPairMatcher;
import stupaq.vhdl93.VHDL93ParserConstants;
import stupaq.vhdl93.ast.NodeToken;

public class TokenSeparatingTreeFormatter extends UserDefinedTreeFormatter
    implements VHDL93ParserConstants {
  private final TokenMatchingActionExecutor preExecutor = new TokenMatchingActionExecutor();

  public TokenSeparatingTreeFormatter(int indentAmt, int wrapWidth) {
    super(indentAmt, wrapWidth);
    preExecutor.put(new TokenPairMatcher() {
      @Override
      public boolean matches(NodeToken left, NodeToken right) {
        int l = left.kind, r = right.kind;
        return (!(l == PERIOD || l == LPAREN || l == RPAREN) &&
            !(r == SEMICOLON || r == PERIOD || r == LPAREN || r == RPAREN)) || (l == ASSIGN) ||
            (r == ASSIGN) || (l == LE) || (r == LE) || (r == IS) || (l == PROCESS && r == LPAREN);
      }
    }, new Action() {
      @Override
      public void execute() {
        ensureWhiteSpace();
      }
    });
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
    preExecutor.nextToken(n);
    super.visit(n);
  }
}
