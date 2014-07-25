package stupaq.vhdl93.formatting;

import stupaq.vhdl93.VHDL93ParserConstants;
import stupaq.vhdl93.ast.NodeToken;
import stupaq.vhdl93.formatting.TokenMatchingActionExecutor.Action;
import stupaq.vhdl93.formatting.TokenMatchingActionExecutor.TokenPairMatcher;
import stupaq.vhdl93.visitor.FormatCommand;

public class TokenSeparatingTreeFormatter extends UserDefinedTreeFormatter
    implements VHDL93ParserConstants {
  private final TokenMatchingActionExecutor preExecutor = new TokenMatchingActionExecutor();

  public TokenSeparatingTreeFormatter(int indentAmt, int wrapWidth) {
    super(indentAmt, wrapWidth);
    // We default to at least one space between tokens.
    preExecutor.put(new TokenPairMatcher() {
      @Override
      public boolean matches(NodeToken left, NodeToken right) {
        return true;
      }
    }, new Action() {
      @Override
      public void execute() {
        ensureWhiteSpace();
      }
    });
    // But in some cases remove it.
    preExecutor.put(new TokenPairMatcher() {
      @Override
      public boolean matches(NodeToken left, NodeToken right) {
        int l = left.kind, r = right.kind;
        return (l == PERIOD || l == LPAREN || l == TICK) ||
            (r == SEMICOLON || r == COMMA || r == PERIOD || r == RPAREN || r == TICK);
      }
    }, new Action() {
      @Override
      public void execute() {
        stripSpaces();
      }
    });
    preExecutor.put(new TokenPairMatcher() {
      @Override
      public boolean matches(NodeToken left, NodeToken right) {
        int l = left.kind, r = right.kind;
        return (l == MAP || l == basic_identifier || l == extended_identifier) && r == LPAREN;
      }
    }, new Action() {
      @Override
      public void execute() {
        stripSpaces();
      }
    });
    // And in other enforce.
    preExecutor.put(new TokenPairMatcher() {
      @Override
      public boolean matches(NodeToken left, NodeToken right) {
        int l = left.kind, r = right.kind;
        return l == ASSIGN || r == ASSIGN || l == LE || r == LE || r == IS || l == ELSIF ||
            l == PROCESS;
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
