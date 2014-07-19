package stupaq.vhdl93.visitor;

import stupaq.vhdl93.ast.NodeToken;

public class PositionResettingVisitor extends DepthFirstVisitor {
  @Override
  public void visit(NodeToken n) {
    n.beginLine = -1;
    n.endLine = -1;
    n.beginColumn = -1;
    n.endColumn = -1;
    n.trimSpecials();
  }
}
