package stupaq.vhdl2lv;

import com.google.common.base.Supplier;

import stupaq.concepts.IOReference;
import stupaq.labview.scripting.hierarchy.FormulaNode;
import stupaq.labview.scripting.hierarchy.FormulaParameter;
import stupaq.labview.scripting.hierarchy.Generic;
import stupaq.labview.scripting.hierarchy.LazyTerminal;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.vhdl93.ast.SimpleNode;
import stupaq.vhdl93.ast.identifier;
import stupaq.vhdl93.ast.primary;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

import static stupaq.vhdl93.ast.ASTGetters.representation;

class ExpressionSourceEmitter extends ExpressionEmitter {
  /** Context of {@link ExpressionSourceEmitter}. */
  private final IOSinks possibleSinks;
  /** Context of {@link #emit(SimpleNode)}. */
  private FormulaNode formula;

  public ExpressionSourceEmitter(Generic owner, IOSinks possibleSinks) {
    super(owner);
    this.possibleSinks = possibleSinks;
  }

  @Override
  protected Terminal emit(SimpleNode n) {
    formula = new FormulaNode(owner, representation(n), "");
    n.accept(this);
    Terminal rvalue = formula.addOutput("<rvalue>");
    formula = null;
    return rvalue;
  }

  @Override
  public void visit(primary n) {
    final FormulaNode formula = ExpressionSourceEmitter.this.formula;
    n.accept(new DepthFirstVisitor() {
      @Override
      public void visit(identifier n) {
        final IOReference ref = new IOReference(representation(n));
        LOGGER.debug("Possible terminal for: " + ref);
        Terminal<FormulaParameter> terminal =
            new LazyTerminal<>(new Supplier<Terminal<FormulaParameter>>() {
              @Override
              public Terminal<FormulaParameter> get() {
                return formula.addInput(ref.toString());
              }
            });
        possibleSinks.put(ref, terminal);
      }
    });
  }
}
