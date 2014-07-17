package stupaq.vhdl2lv;

import com.google.common.base.Optional;

import stupaq.naming.IOReference;
import stupaq.vhdl93.ast.expression;
import stupaq.vhdl93.ast.identifier;
import stupaq.vhdl93.ast.name;
import stupaq.vhdl93.ast.simple_expression;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

abstract class LValueVisitor extends DepthFirstVisitor {
  private final Optional<RValueVisitor> rvalueVisitor;

  public LValueVisitor() {
    rvalueVisitor = Optional.absent();
  }

  public LValueVisitor(RValueVisitor rvalueVisitor) {
    this.rvalueVisitor = Optional.of(rvalueVisitor);
  }

  protected abstract void topLevel(IOReference n);

  @Override
  public void visit(name n) {
    n.accept(new DepthFirstVisitor() {
      @Override
      public void visit(identifier n) {
        topLevel(new IOReference(n));
      }

      @Override
      public void visit(simple_expression n) {
        if (rvalueVisitor.isPresent()) {
          rvalueVisitor.get().visit(n);
        }
      }

      @Override
      public void visit(expression n) {
        if (rvalueVisitor.isPresent()) {
          rvalueVisitor.get().visit(n);
        }
      }
    });
  }

  @Override
  public void visit(identifier n) {
    topLevel(new IOReference(n));
  }
}
