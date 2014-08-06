package stupaq.translation.lv2vhdl.wiring;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

import stupaq.labview.UID;
import stupaq.translation.parsing.NodeRepr;

import static stupaq.translation.errors.LocalisedSemanticException.semanticCheck;
import static stupaq.translation.parsing.NodeRepr.repr;

public class Endpoint {
  private static final Logger LOGGER = LoggerFactory.getLogger(Endpoint.class);
  private final UID uid;
  private final boolean isSource;
  private final Set<Endpoint> connected = Sets.newHashSet();
  private String name;
  private Optional<NodeRepr> value = Optional.absent();

  public Endpoint(UID uid, boolean isSource, String name) {
    this.uid = uid;
    this.isSource = isSource;
    this.name = name;
  }

  public String name() {
    return name;
  }

  public NodeRepr nameRepr() {
    return repr(name());
  }

  public void rename(String name) {
    this.name = name;
  }

  public boolean isSource() {
    return isSource;
  }

  private void valueInternal(NodeRepr value) {
    if (LOGGER.isDebugEnabled()) {
      if (hasValue()) {
        LOGGER.debug("Overriding <{}> from <{}> to <{}>", name, this.value.get(), value);
      } else {
        LOGGER.debug("Setting <{}> to <{}>", name, value);
      }
    }
    this.value = Optional.of(value);
  }

  public void valueOverride(NodeRepr value) {
    valueInternal(value);
  }

  public void valueIfEmpty(NodeRepr value) {
    if (!hasValue()) {
      valueInternal(value);
    }
  }

  public void value(NodeRepr value) {
    semanticCheck(!hasValue(), "Multiple value specifications for terminal.");
    valueInternal(value);
  }

  public NodeRepr value() {
    return value.get();
  }

  public void addConnected(Endpoint other) {
    Preconditions.checkArgument(isSource ^ other.isSource);
    connected.add(other);
  }

  public boolean removeConnected(Endpoint other) {
    return connected.remove(other);
  }

  public Iterable<Endpoint> connected() {
    return Collections.unmodifiableCollection(connected);
  }

  public Endpoint onlyConnected() {
    semanticCheck(connected.size() == 1,
        "Terminal is expected to be connected with only one other terminal.");
    return connected.iterator().next();
  }

  public boolean hasValue() {
    return value.isPresent();
  }

  public String toString() {
    return getClass().getSimpleName() + "{uid=" + uid +
        (hasValue() ? ", value=<" + value.get() + '>' : "") + '}';
  }
}
