package stupaq.translation.lv2vhdl;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.VerifyException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import stupaq.labview.parsing.NeverThrownException;
import stupaq.labview.UID;
import stupaq.labview.parsing.NoOpVisitor;

import static stupaq.SemanticException.semanticCheck;

class EndpointWiringRules extends NoOpVisitor<NeverThrownException> {
  private static final Logger LOGGER = LoggerFactory.getLogger(EndpointWiringRules.class);
  private final Multimap<UID, Endpoint> wiresToEndpoints =
      Multimaps.newListMultimap(Maps.<UID, Collection<Endpoint>>newHashMap(),
          new Supplier<List<Endpoint>>() {
            @Override
            public List<Endpoint> get() {
              return Lists.newArrayList();
            }
          });
  private final EndpointsMap terminals;
  private final Map<Endpoint, Endpoint> redirects = Maps.newHashMap();

  public EndpointWiringRules(EndpointsMap terminals) {
    this.terminals = terminals;
  }

  @Override
  public Iterable<String> parsersOrder() {
    throw new VerifyException();
  }

  @Override
  public void Terminal(UID ownerUID, UID uid, UID wireUID, boolean isSource, String name) {
    Endpoint terminal = new Endpoint(uid, isSource, name);
    terminals.put(uid, terminal);
    wiresToEndpoints.put(wireUID, terminal);
  }

  @Override
  public void Wire(UID ownerUID, UID uid, Optional<String> label) {
    // We know that there will be no more terminals.
    Collection<Endpoint> terms = wiresToEndpoints.removeAll(uid);
    for (Endpoint term1 : terms) {
      for (Endpoint term2 : terms) {
        if (term1.isSource() ^ term2.isSource()) {
          term1.addConnected(term2);
        }
        // Otherwise do nothing.
        // Note that this can happen for single source and multiple sinks,
        // as wires in LV are undirected and the graph itself is a multi-graph.
      }
    }
    if (label.isPresent()) {
      for (Endpoint term : terms) {
        term.value(label.get());
      }
    }
  }

  @Override
  public void Tunnel(UID ownerUID, List<UID> insideTermUIDs, UID outsideTermUID) {
    semanticCheck(insideTermUIDs.size() == 1, "Tunnel has multiple internal frames.");
    Endpoint inside = terminals.get(insideTermUIDs.get(0));
    Endpoint outside = terminals.get(outsideTermUID);
    LOGGER.debug("Tunnel with endpoints: inside: {} outside: {}", inside, outside);
    for (Endpoint term1 : inside.connected()) {
      for (Endpoint term2 : outside.connected()) {
        LOGGER.debug("Terminals connected through tunnel: {} and: {}", term1, term2);
        if (term1.isSource() ^ term2.isSource()) {
          LOGGER.debug("Closure for terminals: {} and: {}", term1, term2);
          term1.addConnected(term2);
        }
      }
    }
    // FIXME place cables, remove these terminals
  }
}
