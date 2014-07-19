package stupaq.lv2vhdl;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;

import stupaq.vhdl93.ast.expression;

import static stupaq.SemanticException.semanticCheck;

abstract class Endpoint implements Iterable<Endpoint> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Endpoint.class);
  private final String name;
  private Optional<expression> value = Optional.absent();
  private Set<Endpoint> connected = Sets.newHashSet();

  public Endpoint(String name) {
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
      LOGGER.warn("Overriding endpoint: {} old: {} new: {}", name, value().get(), value);
    }
    this.value = Optional.of(value);
  }

  public void valueIfEmpty(expression value) {
    if (hasValue()) {
      LOGGER.warn("Ignoring new value for: {} old: {} new: {}", name, value().get(), value);
    } else {
      this.value = Optional.of(value);
    }
  }

  public void value(expression value) {
    semanticCheck(!hasValue(), "Multiple value specifications for terminal.");
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
}
