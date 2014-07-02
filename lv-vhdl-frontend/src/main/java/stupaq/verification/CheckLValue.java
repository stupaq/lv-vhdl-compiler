package stupaq.verification;

import stupaq.vhdl93.ast.Node;
import stupaq.vhdl93.ast.NodeListOptional;
import stupaq.vhdl93.ast.NodeSequence;
import stupaq.vhdl93.ast.adding_operator;
import stupaq.vhdl93.ast.expression;
import stupaq.vhdl93.ast.factor;
import stupaq.vhdl93.ast.multiplying_operator;
import stupaq.vhdl93.ast.name_expression;
import stupaq.vhdl93.ast.primary;
import stupaq.vhdl93.ast.relation_expression;
import stupaq.vhdl93.ast.shift_expression;
import stupaq.vhdl93.ast.simple_expression;
import stupaq.vhdl93.ast.term;
import stupaq.vhdl93.visitor.DepthFirstVisitor;
import stupaq.vhdl93.visitor.GJNoArguDepthFirst;

class CheckLValue extends DepthFirstVisitor {
  @Override
  public void visit(expression n) {
    if (!n.nodeListOptional.present()) {
      n.relation_expression.accept(this);
    }
  }

  @Override
  public void visit(relation_expression n) {
    if (!n.nodeOptional.present()) {
      n.shift_expression.accept(this);
    }
  }

  @Override
  public void visit(shift_expression n) {
    if (!n.nodeOptional.present()) {
      n.simple_expression.accept(this);
    }
  }

  @Override
  public void visit(simple_expression n) {
    if (!n.nodeOptional.present()) {
      boolean canBeASink = n.nodeListOptional.accept(new GJNoArguDepthFirst<Boolean>() {
        @Override
        public Boolean visit(NodeListOptional n) {
          boolean canBeASink = true;
          for (Node el : n.nodes) {
            canBeASink &= el.accept(this);
          }
          return canBeASink;
        }

        @Override
        public Boolean visit(NodeSequence n) {
          boolean canBeASink = true;
          for (Node el : n.nodes) {
            canBeASink &= el.accept(this);
            // FIXME
          }
          return canBeASink;
        }

        @Override
        public Boolean visit(adding_operator n) {
          return false; // FIXME
        }
      });
      if (canBeASink) {
        n.term.accept(this);
      }
    }
  }

  @Override
  public void visit(term n) {
    super.visit(n);
  }

  @Override
  public void visit(multiplying_operator n) {
    super.visit(n);
  }

  @Override
  public void visit(factor n) {
    super.visit(n);
  }

  @Override
  public void visit(primary n) {
    super.visit(n);
  }

  @Override
  public void visit(name_expression n) {
    // FIXME
  }
}
