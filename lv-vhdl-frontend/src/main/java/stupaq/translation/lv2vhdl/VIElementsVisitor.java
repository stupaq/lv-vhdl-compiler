package stupaq.translation.lv2vhdl;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import stupaq.labview.UID;
import stupaq.labview.parsing.NoOpVisitor;
import stupaq.translation.errors.SyntaxException;
import stupaq.translation.parsing.NodeRepr;
import stupaq.translation.parsing.VHDL93ParserPartial;
import stupaq.vhdl93.ast.constant_declaration;
import stupaq.vhdl93.ast.expression;
import stupaq.vhdl93.ast.signal_declaration;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.FluentIterable.from;
import static stupaq.translation.TranslationConventions.*;
import static stupaq.translation.errors.LocalisedSemanticException.semanticCheck;
import static stupaq.translation.parsing.NodeRepr.repr;
import static stupaq.translation.parsing.VHDL93ParserPartial.Parsers.forString;
import static stupaq.vhdl93.VHDL93ParserConstants.ASSIGN;
import static stupaq.vhdl93.VHDL93ParserConstants.SEMICOLON;
import static stupaq.vhdl93.VHDL93ParserTotal.tokenString;

abstract class VIElementsVisitor<E extends Exception> extends NoOpVisitor<E> {
  private static final Logger LOGGER = LoggerFactory.getLogger(VIElementsVisitor.class);
  private final Set<UID> whileLoops = Sets.newHashSet();
  private final EndpointsMap endpoints;

  protected VIElementsVisitor() {
    endpoints = null;
  }

  protected VIElementsVisitor(EndpointsMap endpoints) {
    this.endpoints = endpoints;
  }

  @Override
  public final void Wire(UID ownerUID, UID uid, Optional<String> label) throws E {
    if (label.isPresent()) {
      try {
        String declString = label.get().trim();
        if (!declString.endsWith(tokenString(SEMICOLON))) {
          declString += tokenString(SEMICOLON);
        }
        NodeRepr repr = repr(declString);
        signal_declaration declaration = repr.as().signal_declaration();
        WireWithSignalDeclaration(uid, repr, declaration);
        return;
      } catch (SyntaxException ignored) {
      }
      try {
        NodeRepr repr = repr(label.get());
        expression expression = repr.as().expression();
        WireWithExpression(uid, repr, expression);
        return;
      } catch (SyntaxException ignored) {
      }
    }
    semanticCheck(!label.isPresent(), "Cannot recognize label of the wire.");
    WirePlain(uid);
  }

  @Override
  public final void WhileLoop(UID owner, UID uid) {
    whileLoops.add(uid);
  }

  @Override
  public final void FormulaNode(UID ownerUID, UID uid, String expression, Optional<String> label,
      List<UID> termUIDs) throws E {
    NodeRepr content = repr(expression);
    if (label.equals(ENTITY_CONTEXT)) {
      FormulaWithEntityContext(uid, content);
      return;
    }
    if (label.equals(ENTITY_EXTRA_DECLARATIONS)) {
      FormulaWithEntityDeclarations(uid, content);
      return;
    }
    if (label.equals(ARCHITECTURE_CONTEXT)) {
      FormulaWithArchitectureContext(uid, content);
      return;
    }
    if (label.equals(ARCHITECTURE_EXTRA_DECLARATIONS)) {
      FormulaWithArchitectureDeclarations(uid, content);
      return;
    }
    if (endpoints == null) {
      LOGGER.debug("Skipping classification for formula: {}.", uid);
      return;
    }
    FluentIterable<Endpoint> parameters = from(termUIDs).transform(new Function<UID, Endpoint>() {
      @Override
      public Endpoint apply(UID uid) {
        return endpoints.get(uid);
      }
    });
    if (whileLoops.contains(ownerUID)) {
      FormulaWithProcessStatement(uid, content, parameters);
      return;
    }
    if (label.isPresent()) {
      try {
        VHDL93ParserPartial parser =
            forString(label.get() + tokenString(ASSIGN) + content + tokenString(SEMICOLON));
        constant_declaration constant = parser.constant_declaration();
        FormulaWithDeclaredConstant(uid, constant, parameters);
        return;
      } catch (SyntaxException ignored) {
      }
    }
    Endpoint lvalue = null, rvalue = null;
    for (Endpoint param : parameters) {
      if (param.name().equals(LVALUE_PARAMETER)) {
        semanticCheck(!param.isSource(), "L-value must be data sink.");
        lvalue = param;
      } else if (param.name().equals(RVALUE_PARAMETER)) {
        semanticCheck(param.isSource(), "R-value must be data source.");
        rvalue = param;
      }
    }
    semanticCheck(lvalue == null || rvalue == null, "Expression cannot be both l- and r-value.");
    if (lvalue != null) {
      FormulaWithLvalue(uid, content, lvalue, parameters.filter(not(equalTo(lvalue))));
    } else if (rvalue != null) {
      FormulaWithRvalue(uid, content, rvalue, parameters.filter(not(equalTo(rvalue))));
    } else {
      FormulaWithConcurrentStatements(uid, content, parameters);
    }
  }

  protected void WirePlain(UID uid) {
  }

  protected void WireWithExpression(UID uid, NodeRepr label, expression expression) {
  }

  protected void WireWithSignalDeclaration(UID uid, NodeRepr label,
      signal_declaration declaration) {
  }

  protected void FormulaWithEntityContext(UID uid, NodeRepr expression) throws E {
  }

  protected void FormulaWithEntityDeclarations(UID uid, NodeRepr expression) throws E {
  }

  protected void FormulaWithArchitectureContext(UID uid, NodeRepr expression) throws E {
  }

  protected void FormulaWithArchitectureDeclarations(UID uid, NodeRepr expression) throws E {
  }

  protected void FormulaWithConcurrentStatements(UID uid, NodeRepr expression,
      Iterable<Endpoint> parameters) throws E {
  }

  protected void FormulaWithProcessStatement(UID uid, NodeRepr expression,
      Iterable<Endpoint> parameters) throws E {
  }

  protected void FormulaWithLvalue(UID uid, NodeRepr expression, Endpoint lvalue,
      Iterable<Endpoint> otherParameters) throws E {
  }

  protected void FormulaWithRvalue(UID uid, NodeRepr expression, Endpoint rvalue,
      Iterable<Endpoint> otherParameters) throws E {
  }

  protected void FormulaWithDeclaredConstant(UID uid, constant_declaration constant,
      Iterable<Endpoint> parameters) throws E {
  }
}
