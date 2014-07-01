package stupaq.vhdl2lv;

import stupaq.labview.scripting.hierarchy.FormulaNode;
import stupaq.labview.scripting.hierarchy.Generic;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.vhdl93.ast.SimpleNode;

import static stupaq.vhdl93.ast.ASTGetters.representation;

class ExpressionSinkEmitter extends ExpressionEmitter {
  public ExpressionSinkEmitter(Generic owner) {
    super(owner);
  }

  @Override
  protected Terminal emit(SimpleNode n) {
    FormulaNode formula = new FormulaNode(owner, representation(n), "");
    return formula.addInput("<lvalue>");
  }
}
