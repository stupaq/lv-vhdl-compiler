package stupaq.translation.naming;

import stupaq.vhdl93.ast.subtype_indication;

public class TypeIndication {
  private final subtype_indication indication;

  public TypeIndication(subtype_indication node) {
    this.indication = node;
  }

  public subtype_indication indication() {
    return indication;
  }

  @Override
  public String toString() {
    return "TypeIndication{" + indication() + '}';
  }
}
