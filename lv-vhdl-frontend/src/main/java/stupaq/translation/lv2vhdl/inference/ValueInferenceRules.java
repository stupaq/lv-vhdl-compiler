package stupaq.translation.lv2vhdl.inference;

import com.google.common.collect.Iterables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stupaq.translation.lv2vhdl.wiring.Endpoint;
import stupaq.translation.parsing.NodeRepr;

import static stupaq.translation.parsing.NodeRepr.repr;

public class ValueInferenceRules {
  private static final Logger LOGGER = LoggerFactory.getLogger(ValueInferenceRules.class);
  private int nextInferredNum = 0;

  public void inferValue(Endpoint terminal) {
    if (terminal.hasValue() || Iterables.isEmpty(terminal.connected())) {
      LOGGER.debug("Skipping value inference (no connections) for: {}.", terminal);
      return;
    }
    NodeRepr value = null;
    for (Endpoint connected : terminal.connected()) {
      if (connected.hasValue()) {
        value = connected.value();
      }
    }
    if (value == null) {
      value = repr(nextInferredName());
    }
    LOGGER.debug("Inferred value: <{}> for: {}", value, terminal);
    terminal.value(value);
  }

  private String nextInferredName() {
    return "inferred" + ++nextInferredNum;
  }
}
