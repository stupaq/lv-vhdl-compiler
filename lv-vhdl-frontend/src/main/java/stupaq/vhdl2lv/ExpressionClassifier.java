package stupaq.vhdl2lv;

import com.google.common.collect.Lists;

import java.io.StringReader;
import java.util.List;

import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.VHDL93Parser;
import stupaq.vhdl93.ast.SimpleNode;
import stupaq.vhdl93.ast.attribute_designator;
import stupaq.vhdl93.ast.identifier;
import stupaq.vhdl93.ast.signature;
import stupaq.vhdl93.ast.suffix;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

import static stupaq.vhdl93.ast.ASTBuilders.sequence;

public class ExpressionClassifier {
  private static String unwrapParentheses(String expression) {
    if (expression.startsWith("(") && expression.endsWith(")")) {
      return unwrapParentheses(expression.substring(1, expression.length() - 1));
    } else {
      return expression;
    }
  }

  public List<identifier> topLevelScopeIdentifiers(SimpleNode n) {
    final List<identifier> identifiers = Lists.newArrayList();
    sequence(n).accept(new TopLevelScopeVisitor() {
      @Override
      public void visit(identifier n) {
        identifiers.add(n);
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

  public static class TopLevelScopeVisitor extends DepthFirstVisitor {
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
  }
}
