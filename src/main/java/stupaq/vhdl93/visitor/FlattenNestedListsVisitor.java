package stupaq.vhdl93.visitor;

import stupaq.MissingFeature;
import stupaq.vhdl93.ast.identifier_list;

public class FlattenNestedListsVisitor extends DepthFirstVisitor {
  @Override
  public void visit(identifier_list n) {
    if (n.nodeListOptional.present()) {
      throw new MissingFeature("Identifier lists should include a single identifier only.");
    }
    super.visit(n);
  }
}
