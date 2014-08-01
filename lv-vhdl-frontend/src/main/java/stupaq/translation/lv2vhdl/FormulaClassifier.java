package stupaq.translation.lv2vhdl;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.VerifyException;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import stupaq.labview.UID;
import stupaq.labview.parsing.NoOpVisitor;
import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.ast.constant_declaration;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.FluentIterable.from;
import static stupaq.translation.SemanticException.semanticCheck;
import static stupaq.translation.TranslationConventions.*;
import static stupaq.translation.lv2vhdl.VHDL93PartialParser.parser;
import static stupaq.vhdl93.VHDL93Parser.tokenString;
import static stupaq.vhdl93.VHDL93ParserConstants.ASSIGN;
import static stupaq.vhdl93.VHDL93ParserConstants.SEMICOLON;

abstract class FormulaClassifier<E extends Exception> extends NoOpVisitor<E> {
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FormulaClassifier.class);
  private final Set<UID> whileLoops = Sets.newHashSet();
  private final EndpointsMap terminals;

  public FormulaClassifier() {
    terminals = null;
  }

  public FormulaClassifier(EndpointsMap terminals) {
    this.terminals = terminals;
  }

  @Override
  public Iterable<String> parsersOrder() {
    throw new VerifyException();
  }

  @Override
  public void WhileLoop(UID owner, UID uid) {
    whileLoops.add(uid);
  }

  @Override
  public final void FormulaNode(UID ownerUID, UID uid, String expression, Optional<String> label,
      List<UID> termUIDs) throws E {
    if (label.equals(ENTITY_CONTEXT)) {
      entityContext(uid, expression);
      return;
    }
    if (label.equals(ENTITY_EXTRA_DECLARATIONS)) {
      entityDeclarations(uid, expression);
      return;
    }
    if (label.equals(ARCHITECTURE_CONTEXT)) {
      architectureContext(uid, expression);
      return;
    }
    if (label.equals(ARCHITECTURE_EXTRA_DECLARATIONS)) {
      architectureDeclarations(uid, expression);
      return;
    }
    if (terminals == null) {
      LOGGER.warn("Cannot classify node: {}, missing endpoints map.", uid);
      return;
    }
    FluentIterable<Endpoint> parameters = from(termUIDs).transform(new Function<UID, Endpoint>() {
      @Override
      public Endpoint apply(UID uid) {
        return terminals.get(uid);
      }
      });
      if (whileLoops.contains(ownerUID)) {
        processStatement(uid, expression, parameters);
        return;
      }
    if (label.isPresent()) {
      try {
        VHDL93PartialParser parser =
            parser(label.get() + tokenString(ASSIGN) + expression + tokenString(SEMICOLON));
        constant_declaration constant = parser.constant_declaration();
        declaredConstant(uid, constant, parameters);
        return;
      } catch (ParseException ignored) {
      }
    }
    Endpoint lvalue = null, rvalue = null;
    for (Endpoint param : parameters) {
      if (param.name().equals(LVALUE_PARAMETER)) {
        semanticCheck(!param.isSource(), uid, "L-value must be data sink.");
        lvalue = param;
      } else if (param.name().equals(RVALUE_PARAMETER)) {
        semanticCheck(param.isSource(), uid, "R-value must be data source.");
        rvalue = param;
      }
    }
    semanticCheck(lvalue == null || rvalue == null, uid,
        "Expression cannot be both l- and r-value.");
    if (lvalue != null) {
      lvalueExpression(uid, expression, lvalue, parameters.filter(not(equalTo(lvalue))));
    } else if (rvalue != null) {
      rvalueExpression(uid, expression, rvalue, parameters.filter(not(equalTo(rvalue))));
    } else {
      concurrentStatements(uid, expression, parameters);
    }
  }

  protected void entityContext(UID uid, String expression) throws E {
  }

  protected void entityDeclarations(UID uid, String expression) throws E {
  }

  protected void architectureContext(UID uid, String expression) throws E {
  }

  protected void architectureDeclarations(UID uid, String expression) throws E {
  }

  protected void concurrentStatements(UID uid, String expression, Iterable<Endpoint> parameters)
      throws E {
  }

  protected void processStatement(UID uid, String expression, Iterable<Endpoint> parameters)
      throws E {
  }

  protected void lvalueExpression(UID uid, String expression, Endpoint lvalue,
      Iterable<Endpoint> otherParameters) throws E {
  }

  protected void rvalueExpression(UID uid, String expression, Endpoint rvalue,
      Iterable<Endpoint> otherParameters) throws E {
  }

  protected void declaredConstant(UID uid, constant_declaration constant,
      Iterable<Endpoint> parameters) throws E {
  }
}
