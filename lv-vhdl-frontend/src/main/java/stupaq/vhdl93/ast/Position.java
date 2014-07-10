package stupaq.vhdl93.ast;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class Position {
  public final int beginLine, beginColumn;

  public Position(int beginLine, int beginColumn, int endLine, int endColumn) {
    Preconditions.checkArgument(beginLine >= 0);
    this.beginLine = beginLine;
    this.beginColumn = beginColumn;
  }

  public static Optional<Position> extract(NodeToken n) {
    if (n.beginLine >= 0) {
      return Optional.of(new Position(n.beginLine, n.beginColumn, n.endLine, n.endColumn));
    } else {
      return Optional.absent();
    }
  }

  @Override
  public String toString() {
    return "line: " + beginLine + (beginColumn >= 0 ? ", column: " + beginColumn : "");
  }
}
