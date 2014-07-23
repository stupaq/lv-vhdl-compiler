package stupaq.translation.semantic;

import stupaq.vhdl93.ast.identifier_list;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

import static stupaq.translation.MissingFeatureException.missingIf;

public class FlattenNestedListsVisitor extends DepthFirstVisitor {
  @Override
  public void visit(identifier_list n) {
    missingIf(n.nodeListOptional.present(), n,
        "Identifier lists should include a single identifier only.");
    super.visit(n);
  }
}
