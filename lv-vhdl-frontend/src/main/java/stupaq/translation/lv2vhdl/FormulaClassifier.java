package stupaq.translation.lv2vhdl;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.VerifyException;
import com.google.common.collect.FluentIterable;

import org.slf4j.LoggerFactory;

import java.util.List;

import stupaq.labview.UID;
import stupaq.labview.parsing.NoOpVisitor;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.FluentIterable.from;
import static stupaq.translation.SemanticException.semanticCheck;
import static stupaq.translation.TranslationConventions.*;

abstract class FormulaClassifier<E extends Exception> extends NoOpVisitor<E> {
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FormulaClassifier.class);
  private final EndpointsMap terminals;

  public FormulaClassifier(EndpointsMap terminals) {
    this.terminals = terminals;
  }

  @Override
  public Iterable<String> parsersOrder() {
    throw new VerifyException();
  }

  @Override
  public final void FormulaNode(UID ownerUID, UID uid, String expression, Optional<String> label,
      List<UID> termUIDs) throws E {
    FluentIterable<Endpoint> parameters = from(termUIDs).transform(new Function<UID, Endpoint>() {
      @Override
      public Endpoint apply(UID uid) {
        return terminals.get(uid);
      }
    });
    if (label.equals(ENTITY_CONTEXT)) {
      entityContext(uid, expression);
    } else if (label.equals(ENTITY_EXTRA_DECLARATIONS)) {
      entityDeclarations(uid, expression);
    } else if (label.equals(ARCHITECTURE_CONTEXT)) {
      architectureContext(uid, expression);
    } else if (label.equals(ARCHITECTURE_EXTRA_DECLARATIONS)) {
      architectureDeclarations(uid, expression);
    } else if (label.equals(ARCHITECTURE_EXTRA_STATEMENTS)) {
      concurrentStatements(uid, expression, parameters);
    } else if (label.equals(PROCESS_STATEMENT)) {
      processStatement(uid, expression, parameters);
    } else {
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
}
