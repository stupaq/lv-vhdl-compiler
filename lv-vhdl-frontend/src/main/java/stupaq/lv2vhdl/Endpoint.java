package stupaq.lv2vhdl;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.Iterator;
import java.util.Set;

import stupaq.labview.UID;
import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.VHDL93Parser;
import stupaq.vhdl93.ast.expression;

import static stupaq.SemanticException.semanticCheck;

class Endpoint implements Iterable<Endpoint> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Endpoint.class);
  private final UID uid;
  private final boolean isSource;
  private final String name;
  private Optional<String> value = Optional.absent();
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

  public expression value() throws ParseException {
    VHDL93Parser parser = new VHDL93Parser(new StringReader(this.value.get()));
    expression value = parser.expression();
    parser.eof();
    return value;
  }

  private void valueInternal(String valueString) {
    if (LOGGER.isDebugEnabled()) {
      if (hasValue()) {
        LOGGER.debug("Overriding <{}> from <{}> to <{}>", name, this.value.get(), valueString);
      } else {
        LOGGER.debug("Setting <{}> to <{}>", name, valueString);
      }
    }
    this.value = Optional.of(valueString);
  }

  public void valueOverride(String valueString) {
    valueInternal(valueString);
  }

  public void valueIfEmpty(String valueString) {
    if (!hasValue()) {
      valueInternal(valueString);
    }
  }

  public void value(String valueString) {
    semanticCheck(!hasValue(), "Multiple value specifications for terminal.");
    valueInternal(valueString);
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
    return value.isPresent();
  }

  public String toString() {
    return getClass().getSimpleName() + "{uid=" + uid +
        (hasValue() ? ", value=<" + value.get() + '>' : "") + '}';
  }
}
