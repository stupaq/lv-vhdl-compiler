package stupaq.vhdl2lv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stupaq.labview.scripting.hierarchy.Generic;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.vhdl93.ast.SimpleNode;
import stupaq.vhdl93.ast.expression;
import stupaq.vhdl93.ast.simple_expression;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

abstract class ExpressionEmitter extends DepthFirstVisitor {
  protected static final Logger LOGGER = LoggerFactory.getLogger(ExpressionEmitter.class);
  /** Context of {@link ExpressionEmitter}. */
  protected final Generic owner;

  protected ExpressionEmitter(Generic owner) {
    this.owner = owner;
  }

  protected abstract Terminal emit(SimpleNode n);

  public Terminal emit(expression n) {
    return emit((SimpleNode) n);
  }

  public Terminal emit(simple_expression n) {
    return emit((SimpleNode) n);
  }
}
