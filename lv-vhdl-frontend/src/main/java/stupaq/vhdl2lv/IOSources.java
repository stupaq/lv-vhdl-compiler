package stupaq.vhdl2lv;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.Collection;
import java.util.List;

import stupaq.concepts.IOReference;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.vhdl93.ast.SimpleNode;

class IOSources extends ForwardingMultimap<IOReference, Endpoint> {
  private final Multimap<IOReference, Endpoint> sinks =
      Multimaps.newListMultimap(Maps.<IOReference, Collection<Endpoint>>newHashMap(),
          new Supplier<List<Endpoint>>() {
            @Override
            public List<Endpoint> get() {
              return Lists.newArrayList();
            }
          });

  @Override
  protected Multimap<IOReference, Endpoint> delegate() {
    return sinks;
  }

  public boolean put(IOReference ref, Terminal terminal) {
    return put(ref, new Endpoint(terminal));
  }

  public boolean put(IOReference ref, Terminal terminal, String label) {
    return put(ref, new Endpoint.LabelledEndpoint(terminal, label));
  }

  public boolean put(IOReference ref, Terminal terminal, SimpleNode node) {
    return put(ref, new Endpoint.ExpressionEndpoint(terminal, node));
  }

  public boolean put(IOReference ref, Terminal terminal, Optional<SimpleNode> node) {
    return put(ref, node.isPresent() ? new Endpoint.ExpressionEndpoint(terminal, node.get())
        : new Endpoint(terminal));
  }
}
