package stupaq.translation.vhdl2lv;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import stupaq.labview.hierarchy.Formula;
import stupaq.labview.hierarchy.FormulaNode;
import stupaq.labview.hierarchy.Generic;
import stupaq.labview.hierarchy.Terminal;
import stupaq.translation.TranslationConventions;
import stupaq.translation.naming.IOReference;
import stupaq.translation.semantic.ExpressionClassifier;
import stupaq.translation.semantic.LValueVisitor;
import stupaq.vhdl93.ast.SimpleNode;
import stupaq.vhdl93.ast.expression;

class SinkEmitter {
  private static final Logger LOGGER = LoggerFactory.getLogger(SinkEmitter.class);
  private final Generic owner;
  private final IOSinks danglingSinks;
  private final IOSources namedSources;
  private final Set<Object> blacklist;

  public SinkEmitter(Generic owner, IOSinks danglingSinks, IOSources namedSources) {
    this.owner = owner;
    this.danglingSinks = danglingSinks;
    this.namedSources = namedSources;
    blacklist = Sets.newHashSet();
  }

  private Terminal emitAsLValue(SimpleNode n) {
    Formula formula = new FormulaNode(owner, n.representation(), Optional.<String>absent());
    addTerminals(formula, new SourceEmitter(owner, danglingSinks, namedSources), n);
    formula.cleanupFormula();
    return formula.addInput(TranslationConventions.LVALUE_PARAMETER);
  }

  private void emitAsIdentifier(Terminal source, IOReference ref) {
    namedSources.put(ref, source, ref.toString());
  }

  private void emitAsReference(Terminal source, IOReference ref, SimpleNode n) {
    namedSources.put(ref, source, n.representation());
  }

  public void emitWithSource(Terminal source, expression n) {
    List<IOReference> references = ExpressionClassifier.topLevelReferences(n);
    if (references.isEmpty()) {
      LOGGER.error("Sink expression: {} is a constant", n.representation());
    } else if (references.size() == 1) {
      IOReference ref = references.get(0);
      if (ExpressionClassifier.isIdentifier(n)) {
        emitAsIdentifier(source, ref);
      } else {
        emitAsReference(source, ref, n);
      }
    } else {
      Terminal sink = emitAsLValue(n);
      source.connectTo(sink, Optional.<String>absent());
    }
  }

  public void addTerminals(final Formula formula, final SourceEmitter sourceEmitter, SimpleNode n) {
    n.accept(new LValueVisitor(sourceEmitter.addTerminalsVisitor(formula)) {
      @Override
      protected void topLevel(final IOReference ref) {
        LOGGER.debug("Reference: {} occurs as l-value", ref);
        if (!blacklist.contains(ref)) {
          blacklist.add(ref);
          LOGGER.debug("Reference: {} added as l-value", ref);
          Terminal terminal = formula.addOutput(ref.toString());
          namedSources.put(ref, terminal);
        }
      }
    });
  }
}
