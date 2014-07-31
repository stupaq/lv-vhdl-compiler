package stupaq.translation.vhdl2lv;

import stupaq.vhdl93.ast.interface_signal_declaration;

abstract class SignalDeclaration extends TypedReferenceDeclaration {
  public SignalDeclaration(interface_signal_declaration node) {
    super(node.identifier_list.identifier, node.subtype_indication);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{reference=" + reference() + ", type=" + type() + '}';
  }
}
