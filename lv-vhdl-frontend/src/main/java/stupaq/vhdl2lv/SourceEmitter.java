package stupaq.vhdl2lv;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import stupaq.concepts.IOReference;
import stupaq.labview.scripting.hierarchy.Formula;
import stupaq.labview.scripting.hierarchy.FormulaNode;
import stupaq.labview.scripting.hierarchy.FormulaParameter;
import stupaq.labview.scripting.hierarchy.Generic;
import stupaq.labview.scripting.hierarchy.LazyTerminal;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.vhdl93.ast.SimpleNode;
import stupaq.vhdl93.ast.attribute_designator;
import stupaq.vhdl93.ast.identifier;
import stupaq.vhdl93.ast.signature;
import stupaq.vhdl93.ast.suffix;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

import static stupaq.vhdl93.ast.ASTGetters.representation;

class SourceEmitter {
  private static final Logger LOGGER = LoggerFactory.getLogger(SourceEmitter.class);
  public static final String RVALUE_LABEL = "RESULT";
  private final Generic owner;
  private final IOSinks danglingSinks;
  private final Set<IOReference> blacklist;

  public SourceEmitter(Generic owner, IOSinks danglingSinks) {
    this.owner = owner;
    this.danglingSinks = danglingSinks;
    blacklist = Sets.newHashSet();
  }

  public Terminal emitFormula(SimpleNode n) {
    return emitFormula(n, true);
  }

  public Terminal emitFormula(SimpleNode n, boolean emitTerminals) {
    Formula formula = new FormulaNode(owner, representation(n), Optional.<String>absent());
    if (emitTerminals) {
      addTerminals(formula, n);
    }
    return formula.addOutput(RVALUE_LABEL);
  }

  public void addTerminals(final Formula formula, SimpleNode n) {
    n.accept(new DepthFirstVisitor() {
      @Override
      public void visit(identifier n) {
        final IOReference ref = new IOReference(n);
        LOGGER.debug("Possible terminal: {}", ref);
        if (!blacklist.contains(ref)) {
          blacklist.add(ref);
          Terminal<FormulaParameter> terminal =
              new LazyTerminal<>(new Supplier<Terminal<FormulaParameter>>() {
                @Override
                public Terminal<FormulaParameter> get() {
                  return formula.addInput(ref.toString());
                }
              });
          danglingSinks.put(ref, terminal);
        }
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
    });
  }
}
