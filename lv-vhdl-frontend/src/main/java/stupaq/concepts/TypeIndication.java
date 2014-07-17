package stupaq.concepts;

import stupaq.vhdl93.ast.subtype_indication;

public class TypeIndication {
  private String identifier;

  public TypeIndication(subtype_indication node) {
    this.identifier = node.representation();
  }

  public String identifier() {
    return identifier;
  }

  @Override
  public String toString() {
    return "TypeIndication{" + identifier() + '}';
  }
}
