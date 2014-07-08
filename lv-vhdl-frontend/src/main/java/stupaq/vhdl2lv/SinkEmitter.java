package stupaq.vhdl2lv;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import stupaq.concepts.IOReference;
import stupaq.labview.scripting.hierarchy.Formula;
import stupaq.labview.scripting.hierarchy.FormulaNode;
import stupaq.labview.scripting.hierarchy.Generic;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.labview.scripting.hierarchy.Wire;
import stupaq.vhdl2lv.ExpressionClassifier.TopLevelScopeVisitor;
import stupaq.vhdl93.ast.SimpleNode;
import stupaq.vhdl93.ast.expression;
import stupaq.vhdl93.ast.identifier;
import stupaq.vhdl93.ast.name;
import stupaq.vhdl93.ast.primary;
import stupaq.vhdl93.ast.simple_expression;
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
  private final ExpressionClassifier classifier;

  public SinkEmitter(Generic owner, IOSinks danglingSinks,
      IOSources namedSources) {
    this.owner = owner;
    this.danglingSinks = danglingSinks;
    this.namedSources = namedSources;
    blacklist = Sets.newHashSet();
    classifier = new ExpressionClassifier();
  }

  public Terminal emitAsLValue(SimpleNode n) {
    Formula formula = new FormulaNode(owner, representation(n), Optional.<String>absent());
    addTerminals(formula, new SourceEmitter(owner, danglingSinks, namedSources), n);
    return formula.addInput(LVALUE_LABEL);
  }

  public void emitAsIdentifier(Terminal source, IOReference ref) {
    namedSources.put(ref, source, ref.toString());
  }

  public void emitAsReference(Terminal source, IOReference ref, SimpleNode n) {
    namedSources.put(ref, source, representation(n));
  }

  public void emitWithSource(Terminal source, expression n) {
    List<identifier> identifiers = classifier.topLevelScopeIdentifiers(n);
    if (identifiers.isEmpty()) {
      LOGGER.error("Sink expression: {} is a constant", representation(n));
    } else if (identifiers.size() == 1) {
      IOReference ref = new IOReference(identifiers.get(0));
      if (classifier.isIdentifier(n)) {
        emitAsIdentifier(source, ref);
      } else {
        emitAsReference(source, ref, n);
      }
    } else {
      Terminal sink = emitAsLValue(n);
      new Wire(owner, source, sink, Optional.<String>absent());
    }
  }

  public void addTerminals(final Formula formula, final SourceEmitter sourceEmitter, SimpleNode n) {
    final DepthFirstVisitor visitor = new TopLevelScopeVisitor() {
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
