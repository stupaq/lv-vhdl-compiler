package stupaq.translation.lv2vhdl.inference;

import com.google.common.collect.Iterables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stupaq.translation.lv2vhdl.wiring.Endpoint;

public class ValueInferenceRules {
  private static final Logger LOGGER = LoggerFactory.getLogger(ValueInferenceRules.class);
  private int nextInferredNum = 0;

  public void inferValue(Endpoint terminal) {
    if (terminal.hasValue() || Iterables.isEmpty(terminal.connected())) {
      LOGGER.debug("Skipping value inference (no connections) for: {}.", terminal);
      return;
    }
    String valueString = null;
    for (Endpoint connected : terminal.connected()) {
      if (connected.hasValue()) {
        valueString = connected.valueString();
      }
    }
    if (valueString == null) {
      valueString = nextInferredName();
    }
    LOGGER.debug("Inferred value: <{}> for: {}", valueString, terminal);
    terminal.value(valueString);
  }

  private String nextInferredName() {
    return "inferred" + ++nextInferredNum;
  }
}
