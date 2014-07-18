package stupaq.lv2vhdl;

import com.google.common.base.Optional;

import stupaq.vhdl93.ast.expression;

class TerminalMetadata {
  private final boolean isSource;
  private final String name;
  private expression value;
  private boolean isLValue;

  public TerminalMetadata(boolean isSource, String name) {
    this.isSource = isSource;
    this.name = name;
  }

  public boolean isSource() {
    return isSource;
  }

  public String name() {
    return name;
  }

  public Optional<expression> lvalue() {
    return isLValue ? Optional.fromNullable(value) : Optional.<expression>absent();
  }

  public Optional<expression> rvalue() {
    return !isLValue ? Optional.fromNullable(value) : Optional.<expression>absent();
  }

  public void lvalue(expression e) {
    value = e;
    isLValue = true;
  }

  public void rvalue(expression e) {
    value = e;
    isLValue = false;
  }
}
