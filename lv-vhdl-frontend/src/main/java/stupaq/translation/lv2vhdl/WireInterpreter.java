package stupaq.translation.lv2vhdl;

import com.google.common.base.Optional;
import com.google.common.base.VerifyException;

import stupaq.labview.UID;
import stupaq.labview.parsing.NoOpVisitor;
import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.ast.expression;
import stupaq.vhdl93.ast.signal_declaration;

import static stupaq.translation.SemanticException.semanticCheck;
import static stupaq.translation.lv2vhdl.VHDL93ParserPartial.Parsers.forString;
import static stupaq.vhdl93.VHDL93ParserConstants.SEMICOLON;
import static stupaq.vhdl93.VHDL93ParserTotal.tokenString;

abstract class WireInterpreter<E extends Exception> extends NoOpVisitor<E> {
  @Override
  public Iterable<String> parsersOrder() {
    throw new VerifyException();
  }

  @Override
  public final void Wire(UID ownerUID, UID uid, Optional<String> label) throws E {
    if (label.isPresent()) {
      try {
        String declString = label.get().trim();
        if (!declString.endsWith(tokenString(SEMICOLON))) {
          declString += tokenString(SEMICOLON);
        }
        signal_declaration declaration = forString(declString).signal_declaration();
        wireWithSignalDeclaration(uid, label.get(), declaration);
        return;
      } catch (ParseException ignored) {
      }
      try {
        expression expression = forString(label.get()).expression();
        wireWithExpression(uid, label.get(), expression);
        return;
      } catch (ParseException ignored) {
      }
    }
    semanticCheck(!label.isPresent(), "Cannot recognize label of the wire.");
    wirePlain(uid);
  }

  protected void wirePlain(UID uid) {
  }

  protected void wireWithExpression(UID uid, String label, expression expression) {
  }

  protected void wireWithSignalDeclaration(UID uid, String label, signal_declaration declaration) {
  }
}
