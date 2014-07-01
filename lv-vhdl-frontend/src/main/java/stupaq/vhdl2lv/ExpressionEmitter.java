package stupaq.vhdl2lv;

import stupaq.vhdl93.visitor.DepthFirstVisitor;

abstract class ExpressionEmitter extends DepthFirstVisitor {
  static class ExpressionSourceEmitter extends ExpressionEmitter {
  }

  static class ExpressionSinkEmitter extends ExpressionEmitter {
  }
}
