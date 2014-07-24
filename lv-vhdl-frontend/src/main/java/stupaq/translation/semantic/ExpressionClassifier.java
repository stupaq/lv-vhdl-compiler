package stupaq.translation.semantic;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.io.StringReader;
import java.util.List;

import stupaq.translation.naming.IOReference;
import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.VHDL93Parser;
import stupaq.vhdl93.ast.SimpleNode;
import stupaq.vhdl93.ast.identifier;
import stupaq.vhdl93.ast.subtype_indication;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

import static stupaq.vhdl93.ast.Builders.sequence;

public final class ExpressionClassifier {
  private ExpressionClassifier() {
  }

  private static String unwrapParentheses(String expression) {
    while (true) {
      if (expression.startsWith("(") && expression.endsWith(")")) {
        expression = expression.substring(1, expression.length() - 1);
      } else {
        return expression;
      }
    }
  }

  public static List<IOReference> topLevelReferences(SimpleNode n) {
    final List<IOReference> identifiers = Lists.newArrayList();
    sequence(n).accept(new RValueVisitor() {
      @Override
      public void topLevelScope(IOReference ref) {
        identifiers.add(ref);
      }
    });
    return identifiers;
  }

  public static boolean isIdentifier(SimpleNode n) {
    return asIdentifier(n).isPresent();
  }

  public static Optional<identifier> asIdentifier(SimpleNode n) {
    String rep = unwrapParentheses(n.representation());
    try {
      VHDL93Parser parser = new VHDL93Parser(new StringReader(rep));
      identifier id = parser.identifier();
      parser.eof();
      return Optional.of(id);
    } catch (ParseException ignored) {
      return Optional.absent();
    }
  }

  public static boolean isParametrisedType(subtype_indication indication) {
    try {
      indication.accept(new DepthFirstVisitor() {
        @Override
        public void visit(subtype_indication n) {
          if (n.nodeOptional.present() || n.nodeOptional1.present()) {
            throw new VisitorBreakingException();
          }
          // Skip type identifier.
          n.type_name.nodeOptional.accept(this);
        }

        @Override
        public void visit(identifier n) {
          throw new VisitorBreakingException();
        }
      });
      return false;
    } catch (VisitorBreakingException ignored) {
      return true;
    }
  }

  private static class VisitorBreakingException extends RuntimeException {
  }
}
