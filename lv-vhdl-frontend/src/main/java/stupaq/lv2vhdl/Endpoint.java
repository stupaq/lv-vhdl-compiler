package stupaq.lv2vhdl;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.Set;

import stupaq.vhdl93.ast.expression;

import static stupaq.SemanticException.semanticCheck;

abstract class Endpoint implements Iterable<Endpoint> {
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

  public void value(expression value) {
    semanticCheck(!value().isPresent(), "Multiple value specifications for terminal.");
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
}
