package stupaq.vhdl2lv;

import com.google.common.base.Optional;

import java.util.Set;

import stupaq.concepts.IOReference;
import stupaq.labview.scripting.hierarchy.Formula;
import stupaq.labview.scripting.hierarchy.FormulaNode;
import stupaq.labview.scripting.hierarchy.Generic;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.vhdl93.ast.SimpleNode;
import stupaq.vhdl93.ast.attribute_designator;
import stupaq.vhdl93.ast.expression;
import stupaq.vhdl93.ast.identifier;
import stupaq.vhdl93.ast.name;
import stupaq.vhdl93.ast.primary;
import stupaq.vhdl93.ast.signature;
import stupaq.vhdl93.ast.simple_expression;
import stupaq.vhdl93.ast.suffix;
import stupaq.vhdl93.ast.target;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

import static stupaq.vhdl93.ast.ASTGetters.representation;

class ExpressionSinkEmitter extends ExpressionEmitter {
  /** Context of {@link ExpressionSinkEmitter}. */
  private final IOSources namedSources;
  private final ExpressionSourceEmitter sourceEmitter;

  public ExpressionSinkEmitter(Generic owner, IOSinks danglingSinks, IOSources namedSources) {
    super(owner);
    this.namedSources = namedSources;
    this.sourceEmitter = new ExpressionSourceEmitter(owner, danglingSinks);
  }

  public ExpressionSourceEmitter sourceEmitter() {
    return sourceEmitter;
  }

  @Override
  public Terminal formula(SimpleNode n) {
    Formula formula = new FormulaNode(owner, representation(n), Optional.<String>absent());
    terminals(formula, n);
    return formula.addInput("LVALUE");
  }

  @Override
  public void terminals(final Formula formula, final Set<IOReference> blacklist, SimpleNode n) {
    final DepthFirstVisitor visitor = new DepthFirstVisitor() {
      @Override
      public void visit(identifier n) {
        IOReference ref = new IOReference(n);
        LOGGER.debug("Reference: {} as l-value", ref);
        if (!blacklist.contains(ref)) {
          blacklist.add(ref);
          Terminal terminal = formula.addOutput(ref.toString());
          namedSources.put(ref, terminal);
        }
      }

      @Override
      public void visit(expression n) {
        sourceEmitter.terminals(formula, blacklist, n);
      }

      @Override
      public void visit(simple_expression n) {
        sourceEmitter.terminals(formula, blacklist, n);
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
    };
    // We wait until we descent into an l-value, emit it, and then proceed into any found r-value.
    n.accept(new DepthFirstVisitor() {
      @Override
      public void visit(primary n) {
        n.accept(visitor);
      }

      @Override
      public void visit(target n) {
        n.accept(visitor);
      }

      @Override
      public void visit(name n) {
        n.accept(visitor);
      }

      @Override
      public void visit(identifier n) {
        n.accept(visitor);
      }
    });
  }
}
