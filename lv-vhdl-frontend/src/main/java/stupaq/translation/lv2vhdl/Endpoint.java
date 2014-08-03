package stupaq.translation.lv2vhdl;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.Collections;
import java.util.Set;

import stupaq.labview.UID;
import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.VHDL93Parser;
import stupaq.vhdl93.VHDL93ParserTotal;
import stupaq.vhdl93.ast.expression;

import static stupaq.translation.SemanticException.semanticCheck;

class Endpoint {
  private static final Logger LOGGER = LoggerFactory.getLogger(Endpoint.class);
  private final UID uid;
  private final boolean isSource;
  private final Set<Endpoint> connected = Sets.newHashSet();
  private String name;
  private Optional<String> value = Optional.absent();

  public Endpoint(UID uid, boolean isSource, String name) {
    this.uid = uid;
    this.isSource = isSource;
    this.name = name;
  }

  public String name() {
    return name;
  }

  public void rename(String name) {
    this.name = name;
  }

  public boolean isSource() {
    return isSource;
  }

  public expression value() throws ParseException {
    VHDL93Parser parser = new VHDL93ParserTotal(new StringReader(valueString()));
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
    semanticCheck(!hasValue(), uid, "Multiple value specifications for terminal.");
    valueInternal(valueString);
  }

  public String valueString() {
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
    semanticCheck(connected.size() == 1, uid,
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
