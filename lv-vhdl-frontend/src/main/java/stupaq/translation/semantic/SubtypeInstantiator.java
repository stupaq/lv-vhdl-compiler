package stupaq.translation.semantic;

import com.google.common.base.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stupaq.translation.errors.SemanticException;
import stupaq.translation.errors.SyntaxException;
import stupaq.translation.naming.IOReference;
import stupaq.vhdl93.ast.expression;
import stupaq.vhdl93.ast.identifier;
import stupaq.vhdl93.ast.primary;
import stupaq.vhdl93.ast.subtype_indication;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

import static stupaq.translation.parsing.VHDL93ParserPartial.Parsers.forNode;
import static stupaq.vhdl93.VHDL93ParserConstants.LPAREN;
import static stupaq.vhdl93.VHDL93ParserConstants.RPAREN;
import static stupaq.vhdl93.ast.Builders.choice;
import static stupaq.vhdl93.ast.Builders.sequence;
import static stupaq.vhdl93.ast.Builders.token;

public class SubtypeInstantiator {
  private static final Logger LOGGER = LoggerFactory.getLogger(SubtypeInstantiator.class);
  private final InferenceContext context;
  private Optional<subtype_indication> result;

  public SubtypeInstantiator(InferenceContext context) {
    this.context = context;
  }

  public SubtypeInstantiator() {
    this(new InferenceContext());
  }

  public Optional<subtype_indication> apply(subtype_indication orig) {
    subtype_indication type = forNode(orig).subtype_indication();
    type.accept(new BuilderVisitor());
    if (result == null) {
      result = Optional.of(type);
    }
    return result;
  }

  private class BuilderVisitor extends DepthFirstVisitor {
    @Override
    public void visit(subtype_indication n) {
      if (n.nodeOptional.present() || n.nodeOptional1.present()) {
        result = Optional.absent();
      } else {
        n.type_name.nodeOptional.accept(this);
      }
    }

    @Override
    public void visit(identifier n) {
      result = Optional.absent();
    }

    @Override
    public void visit(primary n) {
      Optional<identifier> formal = ExpressionClassifier.asIdentifier(n);
      if (formal.isPresent()) {
        IOReference ref = new IOReference(formal.get());
        expression val = context.get(ref);
        if (val != null) {
          LOGGER.debug("Replacing: {} with: {}", ref, val.representation());
          try {
            n.nodeChoice = choice(forNode(val).name_expression());
            return;
          } catch (SyntaxException ignored) {
          }
          try {
            n.nodeChoice = choice(forNode(val).literal());
            return;
          } catch (SyntaxException ignored) {
          }
          n.nodeChoice = choice(sequence(token(LPAREN), forNode(val).expression(), token(RPAREN)));
          return;
        }
      }
      // Otherwise just proceed as usual.
      n.nodeChoice.choice.accept(this);
    }
  }
}
