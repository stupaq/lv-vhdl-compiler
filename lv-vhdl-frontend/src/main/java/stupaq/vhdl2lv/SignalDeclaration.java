package stupaq.vhdl2lv;

import stupaq.vhdl93.ast.interface_signal_declaration;

import static stupaq.vhdl93.ast.PropertyAccessor.representation;

public class SignalDeclaration extends VHDLElement<interface_signal_declaration> {
  public final TypeIndication type;

  public SignalDeclaration(interface_signal_declaration node) {
    super(node);
    type = new TypeIndication(node.subtype_indication);
  }

  public String identifier() {
    return representation(node().identifier_list);
  }

  public TypeIndication type() {
    return type;
  }

  @Override
  public String toString() {
    return "SignalDeclaration{" + "identifier=" + identifier() + ", type=" + type + '}';
  }
}
