package stupaq.vhdl2lv;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.vhdl93.ast.SimpleNode;

import static stupaq.vhdl93.ast.ASTGetters.representation;

class Endpoint {
  private final Terminal terminal;

  public Endpoint(Terminal terminal) {
    Preconditions.checkNotNull(terminal);
    this.terminal = terminal;
  }

  public Terminal terminal() {
    return terminal;
  }

  public Optional<String> label() {
    return node().transform(new Function<SimpleNode, String>() {
      @Override
      public String apply(SimpleNode input) {
        return representation(input);
      }
    });
  }

  public Optional<SimpleNode> node() {
    return Optional.absent();
  }

  @Override
  public String toString() {
    Optional<String> label = label();
    return getClass().getSimpleName() + "{terminal=" + terminal() +
        (label.isPresent() ? ", label=" + label.get() : "") + '}';
  }

  public static class LabelledEndpoint extends Endpoint {
    private final String label;

    public LabelledEndpoint(Terminal terminal, String label) {
      super(terminal);
      this.label = label;
    }

    public Optional<String> label() {
      return Optional.of(label);
    }
  }

  static class ExpressionEndpoint extends Endpoint {
    private final SimpleNode node;

    public ExpressionEndpoint(Terminal terminal, SimpleNode node) {
      super(terminal);
      this.node = node;
    }

    @Override
    public Optional<SimpleNode> node() {
      return Optional.of(node);
    }
  }
}
