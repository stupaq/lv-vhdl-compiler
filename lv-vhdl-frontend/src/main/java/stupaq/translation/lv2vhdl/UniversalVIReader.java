package stupaq.translation.lv2vhdl;

import com.google.common.base.Optional;
import com.google.common.base.Verify;
import com.google.common.base.VerifyException;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import stupaq.NeverThrownException;
import stupaq.labview.UID;
import stupaq.labview.parsing.NoOpVisitor;

import static stupaq.SemanticException.semanticCheck;

public class UniversalVIReader extends NoOpVisitor<NeverThrownException> {
  private final Map<Endpoint, Multiplexer> multiplexers = Maps.newHashMap();
  private final EndpointsMap terminals;
  private final Map<UID, Endpoint> controlToClusterEndpoint = Maps.newHashMap();

  public UniversalVIReader(EndpointsMap terminals) {
    this.terminals = terminals;
  }

  public Iterable<Endpoint> findMultiplexedConnections(UID controlUID) {
    Endpoint virtual = controlToClusterEndpoint.get(controlUID);
    return virtual == null ? null : Iterables.concat(virtual);
  }

  public Multiplexer findMultiplexer(Endpoint single) {
    return multiplexers.get(single);
  }

  @Override
  public Iterable<String> parsersOrder() {
    throw new VerifyException();
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
    for (Endpoint other : terminals.get(terminalUID)) {
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
