package stupaq.vhdl93.visitor;

import stupaq.MissingFeatureException;
import stupaq.vhdl93.ast.identifier_list;

public class FlattenNestedListsVisitor extends DepthFirstVisitor {
  @Override
  public void visit(identifier_list n) {
    MissingFeatureException.throwIf(n.nodeListOptional.present(), n,
        "Identifier lists should include a single identifier only.");
    super.visit(n);
  }
}
