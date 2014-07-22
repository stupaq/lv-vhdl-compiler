package stupaq.vhdl93.formatting;

import com.google.common.base.Supplier;
import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.Collection;
import java.util.List;

import stupaq.vhdl93.formatting.TokenMatchingActionExecutor.Action;
import stupaq.vhdl93.formatting.TokenMatchingActionExecutor.TokenPairMatcher;
import stupaq.vhdl93.ast.NodeToken;

public class TokenMatchingActionExecutor extends ForwardingMultimap<TokenPairMatcher, Action> {
  private final Multimap<TokenPairMatcher, Action> delegate =
      Multimaps.newListMultimap(Maps.<TokenPairMatcher, Collection<Action>>newLinkedHashMap(),
          new Supplier<List<Action>>() {
            @Override
            public List<Action> get() {
              return Lists.newArrayList();
            }
          });
  private NodeToken lastToken;

  @Override
  protected Multimap<TokenPairMatcher, Action> delegate() {
    return delegate;
  }

  public void nextToken(NodeToken nextToken) {
    if (lastToken != null) {
      for (TokenPairMatcher matcher : delegate.keySet()) {
        if (matcher.matches(lastToken, nextToken)) {
          for (Action action : delegate.get(matcher)) {
            action.execute();
          }
        }
      }
    }
    lastToken = nextToken;
  }

  public interface TokenPairMatcher {
    public boolean matches(NodeToken left, NodeToken right);
  }

  public interface Action {
    public void execute();
  }
}
