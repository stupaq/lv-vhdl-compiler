package stupaq.translation.vhdl2lv;

import com.google.common.collect.Lists;

import java.io.StringReader;
import java.util.List;

import stupaq.translation.naming.IOReference;
import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.VHDL93Parser;
import stupaq.vhdl93.ast.SimpleNode;

import static stupaq.vhdl93.builders.ASTBuilders.sequence;

class ExpressionClassifier {
  private static String unwrapParentheses(String expression) {
    if (expression.startsWith("(") && expression.endsWith(")")) {
      return unwrapParentheses(expression.substring(1, expression.length() - 1));
    } else {
      return expression;
    }
  }

  public List<IOReference> topLevelScopeReferences(SimpleNode n) {
    final List<IOReference> identifiers = Lists.newArrayList();
    sequence(n).accept(new RValueVisitor() {
      @Override
      public void topLevelScope(IOReference ref) {
        identifiers.add(ref);
      }
    });
    return identifiers;
  }

  public boolean isIdentifier(SimpleNode n) {
    String rep = unwrapParentheses(n.representation());
    try {
      VHDL93Parser parser = new VHDL93Parser(new StringReader(rep));
      parser.identifier();
      parser.eof();
      return true;
    } catch (ParseException ignored) {
      return false;
    }
  }
}
