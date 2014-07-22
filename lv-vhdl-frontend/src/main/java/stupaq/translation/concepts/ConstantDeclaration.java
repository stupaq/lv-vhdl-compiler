package stupaq.translation.concepts;

import com.google.common.base.Verify;

import stupaq.vhdl93.ast.interface_constant_declaration;

public class ConstantDeclaration extends TypedReferenceDeclaration {
  public ConstantDeclaration(interface_constant_declaration node) {
    super(node.identifier_list.identifier, node.subtype_indication);
    Verify.verify(!node.identifier_list.nodeListOptional.present(), "List not flattened.");
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{reference=" + reference() + ", type=" + type() + '}';
  }
}
