package stupaq.vhdl2lv;

import stupaq.naming.IOReference;
import stupaq.vhdl93.ast.attribute_designator;
import stupaq.vhdl93.ast.identifier;
import stupaq.vhdl93.ast.procedure_name;
import stupaq.vhdl93.ast.signature;
import stupaq.vhdl93.ast.suffix;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

public abstract class RValueVisitor extends DepthFirstVisitor {
  protected abstract void topLevelScope(IOReference ref);

  @Override
  public void visit(identifier n) {
    topLevelScope(new IOReference(n));
  }

  @Override
  public void visit(attribute_designator n) {
    // We are not interested in identifiers from some internal scope.
  }

  @Override
  public void visit(signature n) {
    // We are not interested in identifiers from some internal scope.
  }

  @Override
  public void visit(suffix n) {
    // We are not interested in identifiers from some internal scope.
  }

  /*
  TODO we cannot differentiate between no-arg function and signal based on syntax.
  @Override
  public void visit(function_name n) {
    // We are not interested in function or procedure names.
  }
  */

  @Override
  public void visit(procedure_name n) {
    // We are not interested in function or procedure names.
  }
}
