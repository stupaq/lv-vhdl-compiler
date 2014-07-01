package stupaq.concepts;

import com.google.common.base.Verify;

import stupaq.vhdl93.ast.interface_constant_declaration;

public class ConstantDeclaration extends TypedReferenceDeclaration<interface_constant_declaration> {
  public ConstantDeclaration(interface_constant_declaration node) {
    super(node, node.identifier_list.identifier, node.subtype_indication);
    Verify.verify(!node.identifier_list.nodeListOptional.present());
  }

  @Override
  public String toString() {
    return "ConstantDeclaration{" + "reference=" + reference + ", type=" + type + '}';
  }
}
