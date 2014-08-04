package stupaq.translation.lv2vhdl.wiring;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Verify;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import com.ni.labview.VIDump;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import stupaq.labview.UID;
import stupaq.labview.hierarchy.Terminal;
import stupaq.labview.hierarchy.Tunnel;
import stupaq.labview.hierarchy.Wire;
import stupaq.labview.parsing.NeverThrownException;
import stupaq.labview.parsing.VIParser;
import stupaq.translation.lv2vhdl.syntax.VIContextualVisitor;
import stupaq.vhdl93.ast.expression;
import stupaq.vhdl93.ast.signal_declaration;

import static java.util.Arrays.asList;
import static stupaq.translation.SemanticException.semanticCheck;

public class EndpointsMap {
  private static final Logger LOGGER = LoggerFactory.getLogger(EndpointsMap.class);
  private final Map<UID, Endpoint> delegate = Maps.newHashMap();

  public EndpointsMap(VIDump theVi) {
    VIParser.visitVI(theVi, new BuilderVisitor());
  }

  public Endpoint get(UID uid) {
    Endpoint endpoint = delegate.get(uid);
    Verify.verifyNotNull(endpoint, "Missing endpoint for uid: %s.", uid);
    return endpoint;
  }

  public void put(UID uid, Endpoint endpoint) {
    Preconditions.checkNotNull(uid);
    Preconditions.checkNotNull(endpoint);
    delegate.put(uid, endpoint);
  }

  private class BuilderVisitor extends VIContextualVisitor<NeverThrownException> {
    private final Multimap<UID, Endpoint> wiresToEndpoints =
        Multimaps.newListMultimap(Maps.<UID, Collection<Endpoint>>newHashMap(),
            new Supplier<List<Endpoint>>() {
              @Override
              public List<Endpoint> get() {
                return Lists.newArrayList();
              }
            });

    @Override
    public Iterable<String> parsersOrder() {
      return asList(Terminal.XML_NAME, Wire.XML_NAME, Tunnel.XML_NAME);
    }

    @Override
    public void Terminal(UID ownerUID, UID uid, UID wireUID, boolean isSource, String name) {
      Endpoint terminal = new Endpoint(uid, isSource, name);
      EndpointsMap.this.put(uid, terminal);
      wiresToEndpoints.put(wireUID, terminal);
    }

    @Override
    protected void WirePlain(UID uid) {
      connectWithWire(uid);
    }

    @Override
    protected void WireWithExpression(UID uid, String label, expression expression) {
      Collection<Endpoint> terms = connectWithWire(uid);
      for (Endpoint term : terms) {
        term.value(label);
      }
    }

    @Override
    protected void WireWithSignalDeclaration(UID uid, String label,
        signal_declaration declaration) {
      Collection<Endpoint> terms = connectWithWire(uid);
      String valueString = declaration.identifier_list.identifier.representation();
      for (Endpoint term : terms) {
        term.value(valueString);
      }
    }

    private Collection<Endpoint> connectWithWire(UID uid) {
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
      return terms;
    }

    @Override
    public void Tunnel(UID ownerUID, UID uid, List<UID> insideTermUIDs, UID outsideTermUID) {
      semanticCheck(insideTermUIDs.size() == 1, uid, "Tunnel has multiple internal frames.");
      Endpoint inside = EndpointsMap.this.get(insideTermUIDs.get(0));
      Endpoint outside = EndpointsMap.this.get(outsideTermUID);
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
    }
  }
}
