package stupaq.translation.lv2vhdl;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Verify;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import stupaq.labview.UID;
import stupaq.labview.hierarchy.Bundler;
import stupaq.labview.hierarchy.ControlCluster;
import stupaq.labview.hierarchy.Unbundler;
import stupaq.translation.errors.TranslationException;

import static java.util.Arrays.asList;
import static stupaq.translation.errors.LocalisedSemanticException.semanticCheck;

class MultiplexersMap {
  private final Map<Endpoint, Multiplexer> multiplexers = Maps.newHashMap();
  private final EndpointsMap terminals;
  private final Map<UID, Endpoint> controlToClusterEndpoint = Maps.newHashMap();

  public MultiplexersMap(EndpointsMap terminals, ParsedVI theVi) {
    this.terminals = terminals;
    theVi.accept(new BuilderVisitor());
  }

  public Iterable<Endpoint> findMultiplexedConnections(UID controlUID) {
    Endpoint virtual = controlToClusterEndpoint.get(controlUID);
    return virtual == null ? null : FluentIterable.from(virtual.connected())
        .transformAndConcat(new Function<Endpoint, Iterable<? extends Endpoint>>() {
          @Override
          public Iterable<? extends Endpoint> apply(Endpoint endpoint) {
            return endpoint.connected();
          }
        });
  }

  public Multiplexer findMultiplexer(Endpoint single) {
    return multiplexers.get(single);
  }

  private class BuilderVisitor extends VIElementsVisitor<TranslationException> {
    @Override
    public Iterable<String> parsersOrder() {
      return asList(Bundler.XML_NAME, Unbundler.XML_NAME, ControlCluster.XML_NAME);
    }

    @Override
    public void Bundler(UID ownerUID, UID uid, UID outputUIDs, List<UID> inputUIDs) {
      Endpoint terminal = terminals.get(outputUIDs);
      Verify.verifyNotNull(terminal);
      multiplexers.put(terminal, Multiplexer.create(terminals, inputUIDs));
    }

    @Override
    public void Unbundler(UID ownerUID, UID uid, UID inputUID, List<UID> outputUIDs) {
      Endpoint terminal = terminals.get(inputUID);
      Verify.verifyNotNull(terminal);
      multiplexers.put(terminal, Multiplexer.create(terminals, outputUIDs));
    }

    @Override
    public void ControlCluster(UID ownerUID, UID uid, Optional<String> label, UID terminalUID,
        boolean isIndicator, List<UID> controlUIDs) {
      Endpoint terminal = terminals.get(terminalUID);
      for (Endpoint other : terminal.connected()) {
        Multiplexer multiplexer = multiplexers.get(other);
        if (multiplexer != null) {
          semanticCheck(multiplexer.size() == controlUIDs.size(),
              "Different size of (un)bundler and clustered control.");
          for (int i = 0, n = controlUIDs.size(); i < n; ++i) {
            UID control = controlUIDs.get(i);
            Endpoint endpoint = multiplexer.get(i);
            controlToClusterEndpoint.put(control, endpoint);
          }
        }
      }
    }
  }
}
