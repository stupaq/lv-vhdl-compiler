package stupaq.vhdl93.visitor;

import com.google.common.collect.Sets;

import java.util.Set;

import stupaq.vhdl93.ast.NodeToken;

public class LineBreakingTreeFormatter extends TokenSeparatingTreeFormatter {
  private static final Set<String> BREAK_AFTER = Sets.newHashSet(";");

  public LineBreakingTreeFormatter(int indentAmt, int wrapWidth) {
    super(indentAmt, wrapWidth);
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

  @Override
  public void visit(NodeToken n) {
    super.visit(n);
    if (BREAK_AFTER.contains(n.tokenImage)) {
      add(force());
    }
  }
}
