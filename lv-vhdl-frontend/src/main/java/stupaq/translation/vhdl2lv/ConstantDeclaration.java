package stupaq.translation.vhdl2lv;

import stupaq.vhdl93.ast.interface_constant_declaration;

class ConstantDeclaration extends TypedReferenceDeclaration {
  public ConstantDeclaration(interface_constant_declaration node) {
    super(node.identifier_list.identifier, node.subtype_indication);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{reference=" + reference() + ", type=" + type() + '}';
  }
}
