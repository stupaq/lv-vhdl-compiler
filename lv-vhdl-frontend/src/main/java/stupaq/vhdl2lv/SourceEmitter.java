package stupaq.vhdl2lv;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import stupaq.concepts.IOReference;
import stupaq.labview.scripting.hierarchy.CompoundArithmetic;
import stupaq.labview.scripting.hierarchy.Formula;
import stupaq.labview.scripting.hierarchy.FormulaNode;
import stupaq.labview.scripting.hierarchy.FormulaParameter;
import stupaq.labview.scripting.hierarchy.Generic;
import stupaq.labview.scripting.hierarchy.LazyTerminal;
import stupaq.labview.scripting.hierarchy.RingConstant;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.labview.scripting.hierarchy.Wire;
import stupaq.labview.scripting.tools.CompoundArithmeticCreate.ArithmeticMode;
import stupaq.labview.scripting.tools.DataRepresentation;
import stupaq.vhdl2lv.ExpressionClassifier.TopLevelScopeVisitor;
import stupaq.vhdl93.ast.SimpleNode;
import stupaq.vhdl93.ast.expression;
import stupaq.vhdl93.ast.identifier;

import static com.google.common.base.Optional.of;
import static stupaq.vhdl93.ast.ASTGetters.representation;

class SourceEmitter {
  public static final String RVALUE_LABEL = "RESULT";
  private static final Logger LOGGER = LoggerFactory.getLogger(SourceEmitter.class);
  private final Generic owner;
  private final IOSinks danglingSinks;
  private final Set<IOReference> blacklist;
  private final ExpressionClassifier classifier;

  public SourceEmitter(Generic owner, IOSinks danglingSinks, IOSources namedSources) {
    this.owner = owner;
    this.danglingSinks = danglingSinks;
    blacklist = Sets.newHashSet();
    classifier = new ExpressionClassifier();
  }

  public static CompoundArithmetic branchNode(Generic owner) {
    return new CompoundArithmetic(owner, ArithmeticMode.ADD, 1, Optional.<String>absent());
  }

  public Terminal emitAsConstant(SimpleNode n, Optional<String> label) {
    RingConstant constant = new RingConstant(owner, Collections.singletonMap(representation(n), 0),
        DataRepresentation.I32, label);
    return constant.endpoint().get();
  }

  public void emitAsIdentifier(IOReference ref, Terminal sink) {
    danglingSinks.put(ref, sink);
  }

  public void emitAsReference(IOReference ref, SimpleNode n, Terminal sink) {
    CompoundArithmetic branch = branchNode(owner);
    new Wire(owner, branch.output(), sink, of(representation(n)));
    danglingSinks.put(ref, branch.inputs().get(0));
  }

  public void emitWithSink(expression n, Terminal sink) {
    List<identifier> identifiers = classifier.topLevelScopeIdentifiers(n);
    if (identifiers.isEmpty()) {
      Terminal source = emitAsConstant(n, Optional.<String>absent());
      new Wire(owner, source, sink, Optional.<String>absent());
    } else if (identifiers.size() == 1) {
      IOReference ref = new IOReference(identifiers.get(0));
      if (classifier.isIdentifier(n)) {
        emitAsIdentifier(ref, sink);
      } else {
        emitAsReference(ref, n, sink);
      }
    } else {
      Terminal source = emitAsExpression(n);
      new Wire(owner, source, sink, Optional.<String>absent());
    }
  }

  public Terminal<FormulaParameter> emitAsExpression(SimpleNode n) {
    Formula formula = new FormulaNode(owner, representation(n), Optional.<String>absent());
    addTerminals(formula, n);
    return formula.addOutput(RVALUE_LABEL);
  }

  public void addTerminals(final Formula formula, SimpleNode n) {
    n.accept(new TopLevelScopeVisitor() {
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
    });
  }
}
