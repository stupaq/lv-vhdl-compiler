package stupaq.vhdl93.formatting;

import stupaq.vhdl93.ast.NodeToken;
import stupaq.vhdl93.formatting.TokenMatchingActionExecutor.Action;
import stupaq.vhdl93.formatting.TokenMatchingActionExecutor.TokenPairMatcher;
import stupaq.vhdl93.visitor.FormatCommand;

public class LineBreakingTreeFormatter extends TokenSeparatingTreeFormatter {
  private final TokenMatchingActionExecutor preExecutor = new TokenMatchingActionExecutor();
  private final TokenMatchingActionExecutor postExecutor = new TokenMatchingActionExecutor();

  public LineBreakingTreeFormatter(int indentAmt, int wrapWidth) {
    super(indentAmt, wrapWidth);
    // Ensure that we have a line break after a semicolon unless there is a following comment.
    preExecutor.put(new TokenPairMatcher() {
      @Override
      public boolean matches(NodeToken left, NodeToken right) {
        int l = left.kind, r = right.kind;
        return (l == SEMICOLON && r != COMMENT);
      }
    }, new Action() {
      @Override
      public void execute() {
        ensureLineBreak();
      }
    });
    // Insert line break before primary or secondary unit.
    preExecutor.put(new TokenPairMatcher() {
      @Override
      public boolean matches(NodeToken left, NodeToken right) {
        int l = left.kind, r = right.kind;
        return (l != END) && (r == ARCHITECTURE) || (r == ENTITY) || (r == LIBRARY);
      }
    }, new Action() {
      @Override
      public void execute() {
        add(force());
      }
    });
    // Break line and indent after block entry.
    preExecutor.put(new TokenPairMatcher() {
      @Override
      public boolean matches(NodeToken left, NodeToken right) {
        int l = left.kind;
        return (l == BEGIN) || (l == THEN) || (l == ELSE);
      }
    }, new Action() {
      @Override
      public void execute() {
        add(indent());
        add(force());
      }
    });
    // Same for some tokens in POST executor.
    postExecutor.put(new TokenPairMatcher() {
      @Override
      public boolean matches(NodeToken left, NodeToken right) {
        int l = left.kind, r = right.kind;
        return (l != END) && (r == LOOP || r == GENERATE);
      }
    }, new Action() {
      @Override
      public void execute() {
        add(indent());
        add(force());
      }
    });
    // Reduce indentation when exiting block.
    preExecutor.put(new TokenPairMatcher() {
      @Override
      public boolean matches(NodeToken left, NodeToken right) {
        int r = right.kind;
        return (r == END) || (r == BEGIN) || (r == ELSE) || (r == ELSIF);
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
    postExecutor.nextToken(n);
  }
}
