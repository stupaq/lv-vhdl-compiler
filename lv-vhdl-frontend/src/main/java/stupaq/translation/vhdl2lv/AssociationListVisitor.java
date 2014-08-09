package stupaq.translation.vhdl2lv;

import com.google.common.base.Preconditions;

import stupaq.translation.naming.IOReference;
import stupaq.vhdl93.ast.actual_part;
import stupaq.vhdl93.ast.actual_part_open;
import stupaq.vhdl93.ast.association_list;
import stupaq.vhdl93.ast.expression;
import stupaq.vhdl93.ast.named_association_element;
import stupaq.vhdl93.ast.named_association_list;
import stupaq.vhdl93.ast.positional_association_element;
import stupaq.vhdl93.ast.positional_association_list;
import stupaq.vhdl93.visitor.NonTerminalsNoOpVisitor;

abstract class AssociationListVisitor<T> extends NonTerminalsNoOpVisitor<T> {
  /** Context of {@link #visit(positional_association_list)}. */
  private int elementIndex;
  /** Context of {@link #visit(named_association_list)}. */
  private IOReference name;

  @Override
  public final void visit(association_list n) {
    n.nodeChoice.accept(this);
  }

  @Override
  public final void visit(named_association_list n) {
    elementIndex = Integer.MIN_VALUE;
    n.named_association_element.accept(this);
    n.nodeListOptional.accept(this);
  }

  @Override
  public final void visit(positional_association_list n) {
    elementIndex = 0;
    n.positional_association_element.accept(this);
    n.nodeListOptional.accept(this);
  }

  @Override
  public final void visit(named_association_element n) {
    Preconditions.checkState(elementIndex == Integer.MIN_VALUE);
    name = new IOReference(n.formal_part.identifier);
    n.actual_part.accept(this);
    name = null;
  }

  @Override
  public final void visit(positional_association_element n) {
    Preconditions.checkState(elementIndex >= 0);
    // We do not harvest context on ports, therefore we know we are dealing with generic.
    n.actual_part.accept(this);
    ++elementIndex;
  }

  @Override
  public final void visit(actual_part n) {
    Preconditions.checkState(name == null ^ elementIndex == Integer.MIN_VALUE);
    n.nodeChoice.choice.accept(this);
  }

  @Override
  public final void visit(actual_part_open n) {
    if (name != null) {
      actualPartOpen(name);
    } else {
      actualPartOpen(elementIndex);
    }
  }

  protected abstract void actualPartOpen(int elementIndex);

  protected abstract void actualPartOpen(IOReference name);

  @Override
  public final void visit(expression n) {
    // Note that we do not visit recursively in current setting, so we are sure,
    // that this is the top-level expression context.
    if (name != null) {
      actualPartExpression(name, n);
    } else {
      actualPartExpression(elementIndex, n);
    }
  }

  protected abstract void actualPartExpression(int elementIndex, expression n);

  protected abstract void actualPartExpression(IOReference name, expression n);
}
