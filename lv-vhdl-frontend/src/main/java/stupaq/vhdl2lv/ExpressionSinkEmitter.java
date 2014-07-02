package stupaq.vhdl2lv;

import com.google.common.collect.Sets;

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
import stupaq.vhdl93.ast.primary;
import stupaq.vhdl93.ast.signature;
import stupaq.vhdl93.ast.simple_expression;
import stupaq.vhdl93.ast.suffix;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

import static stupaq.vhdl93.ast.ASTGetters.representation;

class ExpressionSinkEmitter extends ExpressionEmitter {
  /** Context of {@link ExpressionSinkEmitter}. */
  private final IOSinks danglingSinks;
  /** Context of {@link ExpressionSinkEmitter}. */
  private final IOSources namedSources;

  public ExpressionSinkEmitter(Generic owner, IOSinks danglingSinks, IOSources namedSources) {
    super(owner);
    this.danglingSinks = danglingSinks;
    this.namedSources = namedSources;
  }

  @Override
  protected Terminal emit(SimpleNode n) {
    Formula formula = new FormulaNode(owner, representation(n), "");
    emitTerminals(formula, Sets.<IOReference>newHashSet(), n);
    return formula.addInput("LVALUE");
  }

  public void emitTerminals(final Formula formula, final Set<IOReference> blacklist, SimpleNode n) {
    n.accept(new DepthFirstVisitor() {
      @Override
      public void visit(primary n) {
        n.accept(new DepthFirstVisitor() {
          boolean emitExpressionsAsSources = false;

          @Override
          public void visit(identifier n) {
            IOReference ref = new IOReference(representation(n));
            LOGGER.debug("Reference: {} as l-value", ref);
            if (!blacklist.contains(ref)) {
              blacklist.add(ref);
              Terminal terminal = formula.addOutput(ref.name());
              namedSources.put(ref, terminal);
            }
          }

          @Override
          public void visit(expression n) {
            if (emitExpressionsAsSources) {
              new ExpressionSourceEmitter(owner, danglingSinks).emitTerminals(formula, blacklist,
                  n);
            }
          }

          @Override
          public void visit(attribute_designator n) {
            // We are not interested in identifiers from some internal scope.
          }

          @Override
          public void visit(simple_expression n) {
            if (emitExpressionsAsSources) {
              new ExpressionSourceEmitter(owner, danglingSinks).emitTerminals(formula, blacklist,
                  n);
            }
          }

          @Override
          public void visit(signature n) {
            // We are not interested in identifiers from some internal scope.
          }

          @Override
          public void visit(primary n) {
            boolean saved = emitExpressionsAsSources;
            emitExpressionsAsSources = true;
            super.visit(n);
            emitExpressionsAsSources = saved;
          }

          @Override
          public void visit(suffix n) {
            // We are not interested in identifiers from some internal scope.
          }
        });
      }
    });
  }
}
