package stupaq.vhdl93.ast;

import stupaq.vhdl93.visitor.GJNoArguVisitor;
import stupaq.vhdl93.visitor.GJVisitor;
import stupaq.vhdl93.visitor.GJVoidVisitor;
import stupaq.vhdl93.visitor.Visitor;

public class error_skipto extends SimpleNode implements Node {
  @Override
  public void accept(Visitor v) {
  }

  @Override
  public <R, A> R accept(GJVisitor<R, A> v, A argu) {
    return null;
  }

  @Override
  public <R> R accept(GJNoArguVisitor<R> v) {
    return null;
  }

  @Override
  public <A> void accept(GJVoidVisitor<A> v, A argu) {
  }
}
