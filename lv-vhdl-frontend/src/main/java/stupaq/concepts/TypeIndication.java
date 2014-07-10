package stupaq.concepts;

import stupaq.vhdl93.ast.subtype_indication;

public class TypeIndication extends VHDLElement<subtype_indication> {
  String identifier;

  public TypeIndication(subtype_indication node) {
    super(node);
    identifier = node.representation();
  }

  public String identifier() {
    return identifier;
  }

  @Override
  public String toString() {
    return "TypeIndication{" + "identifier=" + identifier() + '}';
  }
}
