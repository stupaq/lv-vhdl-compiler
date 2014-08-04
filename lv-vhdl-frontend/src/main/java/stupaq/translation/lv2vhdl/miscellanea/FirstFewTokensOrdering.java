package stupaq.translation.lv2vhdl.miscellanea;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import java.util.List;

import stupaq.vhdl93.ast.Node;
import stupaq.vhdl93.ast.NodeToken;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

public class FirstFewTokensOrdering extends Ordering<Node> {
  private final int lookupLimit;

  public FirstFewTokensOrdering(int lookupLimit) {
    this.lookupLimit = lookupLimit;
  }

  @Override
  public int compare(Node o1, Node o2) {
    return Ordering.natural()
        .onResultOf(new Function<NodeToken, Comparable>() {
          @Override
          public Comparable apply(NodeToken o) {
            return o.tokenImage;
          }
        })
        .lexicographical()
        .compare(new TokensFindingVisitor(lookupLimit, o1).tokens(),
            new TokensFindingVisitor(lookupLimit, o2).tokens());
  }

  private static class TokensFindingVisitor extends DepthFirstVisitor {
    private final List<NodeToken> tokens = Lists.newArrayList();
    private int lookupLimit;

    public TokensFindingVisitor(int lookupLimit, Node n) {
      this.lookupLimit = lookupLimit;
      try {
        n.accept(this);
      } catch (AllTokensFoundException ignored) {
      }
    }

    public List<NodeToken> tokens() {
      return tokens;
    }

    @Override
    public void visit(NodeToken n) {
      tokens.add(n);
      if (--lookupLimit <= 0) {
        throw new AllTokensFoundException(n);
      }
    }

    private static class AllTokensFoundException extends RuntimeException {
      private final NodeToken token;

      private AllTokensFoundException(NodeToken token) {
        this.token = token;
      }

      public NodeToken getToken() {
        return token;
      }
    }
  }
}

