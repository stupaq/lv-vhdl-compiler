package stupaq.vhdl2lv;

import stupaq.vhdl93.ast.subtype_indication;

import static stupaq.vhdl93.ast.ASTGetters.representation;

public class TypeIndication extends VHDLElement<subtype_indication> {
  String identifier;

  public TypeIndication(subtype_indication node) {
    super(node);
    identifier = representation(node);
  }

  public String identifier() {
    return identifier;
  }

  @Override
  public String toString() {
    return "TypeIndication{" + "identifier=" + identifier() + '}';
  }
}
