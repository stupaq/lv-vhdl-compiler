package stupaq.vhdl2lv;

import stupaq.vhdl93.ast.interface_constant_declaration;

import static stupaq.vhdl93.ast.PropertyAccessor.representation;

public class ConstantDeclaration extends VHDLElement<interface_constant_declaration> {
  public final TypeIndication type;

  public ConstantDeclaration(interface_constant_declaration node) {
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
    return "ConstantDeclaration{" + "identifier=" + identifier() + ", type=" + type + '}';
  }
}
