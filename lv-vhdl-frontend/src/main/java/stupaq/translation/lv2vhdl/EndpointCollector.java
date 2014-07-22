package stupaq.translation.lv2vhdl;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.VerifyException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.Collection;
import java.util.List;

import stupaq.NeverThrownException;
import stupaq.labview.UID;
import stupaq.labview.parsing.NoOpVisitor;

class EndpointCollector extends NoOpVisitor<NeverThrownException> {
  private final Multimap<UID, Endpoint> wiresToEndpoints =
      Multimaps.newListMultimap(Maps.<UID, Collection<Endpoint>>newHashMap(),
          new Supplier<List<Endpoint>>() {
            @Override
            public List<Endpoint> get() {
              return Lists.newArrayList();
            }
          });
  private final EndpointsMap terminals;

  public EndpointCollector(EndpointsMap terminals) {
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
  public void Tunnel(UID ownerUID, List<UID> insideTermUIDs, UID outsideTermUID) {
    // FIXME
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
      for (Endpoint terminal : terms) {
        terminal.value(label.get());
      }
    }
  }
}
