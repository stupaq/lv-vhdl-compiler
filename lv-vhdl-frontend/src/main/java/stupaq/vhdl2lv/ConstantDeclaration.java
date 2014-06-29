package stupaq.vhdl2lv;

import com.google.common.base.Verify;

import stupaq.vhdl93.ast.interface_constant_declaration;

public class ConstantDeclaration extends HDLElement<interface_constant_declaration> {
  public final TypeIndication type;
  private final IOReference reference;

  public ConstantDeclaration(interface_constant_declaration node) {
    super(node);
    Verify.verify(!node.identifier_list.nodeListOptional.present());
    type = new TypeIndication(node.subtype_indication);
    reference = new IOReference(node().identifier_list.identifier);
  }

  public IOReference reference() {
    return reference;
  }

  public TypeIndication type() {
    return type;
  }

  @Override
  public String toString() {
    return "ConstantDeclaration{" + "reference=" + reference + ", type=" + type + '}';
  }
}
