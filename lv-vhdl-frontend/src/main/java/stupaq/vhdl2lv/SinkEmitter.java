package stupaq.vhdl2lv;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

class SinkEmitter {
  private static final Logger LOGGER = LoggerFactory.getLogger(SinkEmitter.class);
  public static final String LVALUE_LABEL = "ASSIGNEE";
  private final Generic owner;
  private final IOSinks danglingSinks;
  private final IOSources namedSources;
  private final Set<Object> blacklist;

  public SinkEmitter(Generic owner, IOSinks danglingSinks,
      IOSources namedSources) {
    this.owner = owner;
    this.danglingSinks = danglingSinks;
    this.namedSources = namedSources;
    blacklist = Sets.newHashSet();
  }

  public Terminal emitFormula(SimpleNode n) {
    return emitFormula(n, true);
  }

  public Terminal emitFormula(SimpleNode n, boolean emitTerminals) {
    Formula formula = new FormulaNode(owner, representation(n), Optional.<String>absent());
    if (emitTerminals) {
      addTerminals(formula, new SourceEmitter(owner, danglingSinks), n);
    }
    return formula.addInput(LVALUE_LABEL);
  }

  public void addTerminals(final Formula formula, final SourceEmitter sourceEmitter, SimpleNode n) {
    final DepthFirstVisitor visitor = new DepthFirstVisitor() {
      @Override
      public void visit(identifier n) {
        IOReference ref = new IOReference(n);
        LOGGER.debug("Reference: {} occurs as l-value", ref);
        if (!blacklist.contains(ref)) {
          blacklist.add(ref);
          LOGGER.debug("Reference: {} added as l-value", ref);
          Terminal terminal = formula.addOutput(ref.toString());
          namedSources.put(ref, terminal);
        }
      }

      @Override
      public void visit(expression n) {
        sourceEmitter.addTerminals(formula, n);
      }

      @Override
      public void visit(simple_expression n) {
        sourceEmitter.addTerminals(formula, n);
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
