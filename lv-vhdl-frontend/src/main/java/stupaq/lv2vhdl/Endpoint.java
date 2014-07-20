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

abstract class Endpoint implements Iterable<Endpoint> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Endpoint.class);
  private final UID uid;
  private final String name;
  private Optional<expression> value = Optional.absent();
  private Set<Endpoint> connected = Sets.newHashSet();

  public Endpoint(UID uid, String name) {
    this.uid = uid;
    this.name = name;
  }

  public String name() {
    return name;
  }

  public abstract boolean isSource();

  public Optional<expression> value() {
    return value;
  }

  public void valueOverride(expression value) {
    if (hasValue()) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Overriding <{}> from <{}> to <{}>", name, value().get(),
            value.representation());
      }
    }
    this.value = Optional.of(value);
  }

  public void valueIfEmpty(expression value) {
    if (hasValue()) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Ignoring <{}> from <{}> to <{}>", name, value().get(),
            value.representation());
      }
    } else {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Setting <{}> to <{}>", name, value.representation());
      }
      this.value = Optional.of(value);
    }
  }

  public void value(expression value) {
    semanticCheck(!hasValue(), "Multiple value specifications for terminal.");
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Setting <{}> to <{}>", name, value.representation());
    }
    this.value = Optional.of(value);
  }

  public void addConnected(Endpoint other) {
    Preconditions.checkArgument(isSource() ^ other.isSource());
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
    return getClass().getSimpleName() + "{uid=" + uid + '}';
  }

  public static class Source extends Endpoint {
    public Source(UID uid, String name) {
      super(uid, name);
    }

    @Override
    public boolean isSource() {
      return true;
    }
  }

  public static class Sink extends Endpoint {
    public Sink(UID uid, String name) {
      super(uid, name);
    }

    @Override
    public boolean isSource() {
      return false;
    }
  }
}
