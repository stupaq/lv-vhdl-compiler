package stupaq.concepts;

import stupaq.vhdl93.ast.subtype_indication;

public class TypeIndication {
  String identifier;

  public TypeIndication(subtype_indication node) {
    identifier = node.representation();
  }

  public String identifier() {
    return identifier;
  }

  @Override
  public String toString() {
    return "TypeIndication{" + identifier() + '}';
  }
}
