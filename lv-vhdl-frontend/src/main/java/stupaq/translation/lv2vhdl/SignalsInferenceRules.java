package stupaq.translation.lv2vhdl;

import com.google.common.base.Optional;

import stupaq.vhdl93.ast.SimpleNode;

class SignalsInferenceRules {
  public <T extends SimpleNode> Optional<T> inferExpression(Endpoint terminal) {
    return Optional.absent();
  }
}
