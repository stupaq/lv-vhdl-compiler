package stupaq.translation.naming;

import stupaq.vhdl93.ast.subtype_indication;

public class TypeIndication {
  private final String identifier;

  public TypeIndication(subtype_indication node) {
    this.identifier = node.representation();
  }

  public String indication() {
    return identifier;
  }

  @Override
  public String toString() {
    return "TypeIndication{" + indication() + '}';
  }
}
