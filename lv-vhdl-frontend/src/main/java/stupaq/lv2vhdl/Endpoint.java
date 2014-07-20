package stupaq.lv2vhdl;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;

import stupaq.labview.UID;
import stupaq.vhdl93.ast.expression;

import static stupaq.SemanticException.semanticCheck;

class Endpoint implements Iterable<Endpoint> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Endpoint.class);
  private final UID uid;
  private final boolean isSource;
  private final String name;
  private Optional<expression> value = Optional.absent();
  private Set<Endpoint> connected = Sets.newHashSet();

  public Endpoint(UID uid, boolean isSource, String name) {
    this.uid = uid;
    this.isSource = isSource;
    this.name = name;
  }

  public String name() {
    return name;
  }

  public boolean isSource() {
    return isSource;
  }

  public Optional<expression> value() {
    return value;
  }

  private void valueInternal(expression value) {
    if (LOGGER.isDebugEnabled()) {
      if (hasValue()) {
        LOGGER.debug("Overriding <{}> from <{}> to <{}>", name, value().get().representation(),
            value.representation());
      } else {
        LOGGER.debug("Setting <{}> to <{}>", name, value.representation());
      }
    }
    this.value = Optional.of(value);
  }

  public void valueOverride(expression value) {
    valueInternal(value);
  }

  public void valueIfEmpty(expression value) {
    if (!hasValue()) {
      valueInternal(value);
    }
  }

  public void value(expression value) {
    semanticCheck(!hasValue(), "Multiple value specifications for terminal.");
    valueInternal(value);
  }

  public void addConnected(Endpoint other) {
    Preconditions.checkArgument(isSource ^ other.isSource);
    connected.add(other);
  }

  @Override
  public Iterator<Endpoint> iterator() {
    return connected.iterator();
  }

  public boolean hasValue() {
    return value().isPresent();
  }

  public String toString() {
    return getClass().getSimpleName() + "{uid=" + uid +
        (hasValue() ? ", value=<" + value().get().representation() + '>' : "") + '}';
  }
}
