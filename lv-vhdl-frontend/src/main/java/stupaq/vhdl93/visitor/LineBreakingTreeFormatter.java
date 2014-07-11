package stupaq.vhdl93.visitor;

import stupaq.formatting.TokenMatchingActionExecutor;
import stupaq.formatting.TokenMatchingActionExecutor.Action;
import stupaq.formatting.TokenMatchingActionExecutor.TokenPairMatcher;
import stupaq.vhdl93.ast.NodeToken;

public class LineBreakingTreeFormatter extends TokenSeparatingTreeFormatter {
  private final TokenMatchingActionExecutor preExecutor = new TokenMatchingActionExecutor();

  public LineBreakingTreeFormatter(int indentAmt, int wrapWidth) {
    super(indentAmt, wrapWidth);
    preExecutor.put(new TokenPairMatcher() {
      @Override
      public boolean matches(NodeToken left, NodeToken right) {
        int l = left.kind, r = right.kind;
        return (l == SEMICOLON) || (l == IS) || (l == BEGIN);
      }
    }, new Action() {
      @Override
      public void execute() {
        ensureLineBreak();
      }
    });
    preExecutor.put(new TokenPairMatcher() {
      @Override
      public boolean matches(NodeToken left, NodeToken right) {
        int l = left.kind, r = right.kind;
        return (l == IS) || (l == BEGIN) || (l == THEN) || (l == ELSE);
      }
    }, new Action() {
      @Override
      public void execute() {
        add(indent());
        add(force());
      }
    });
    preExecutor.put(new TokenPairMatcher() {
      @Override
      public boolean matches(NodeToken left, NodeToken right) {
        int l = left.kind, r = right.kind;
        return (r == END) || (r == BEGIN) || (l == ELSE) || (l == ELSIF);
      }
    }, new Action() {
      @Override
      public void execute() {
        add(outdent());
      }
    });
  }

  @Override
  protected void add(FormatCommand cmd) {
    if (cmd.getCommand() == FormatCommand.OUTDENT || cmd.getCommand() == FormatCommand.INDENT) {
      int size = cmdQueue.size();
      if (size > 0) {
        FormatCommand last = cmdQueue.lastElement();
        if (last.getCommand() == FormatCommand.FORCE && last.getNumCommands() == 1) {
          cmdQueue.remove(size - 1);
          super.add(cmd);
          super.add(last);
          return;
        }
      }
    }
    super.add(cmd);
  }

  protected final void ensureLineBreak() {
    for (int i = cmdQueue.size() - 1; i >= 0; --i) {
      switch (cmdQueue.get(i).getCommand()) {
        case FormatCommand.FORCE:
          return;
        case FormatCommand.INDENT:
        case FormatCommand.OUTDENT:
        case FormatCommand.SPACE:
          break;
        default:
          i = -1;
      }
    }
    add(force());
  }

  @Override
  public void visit(NodeToken n) {
    preExecutor.nextToken(n);
    super.visit(n);
  }
}
