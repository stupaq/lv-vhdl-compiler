package stupaq.vhdl93.visitor;

import stupaq.vhdl93.ast.identifier_list;

import static stupaq.MissingFeatureException.missingIf;

public class FlattenNestedListsVisitor extends DepthFirstVisitor {
  @Override
  public void visit(identifier_list n) {
    missingIf(n.nodeListOptional.present(), n,
        "Identifier lists should include a single identifier only.");
    super.visit(n);
  }
}
