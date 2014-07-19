package stupaq.concepts;

import com.google.common.base.Verify;

import stupaq.vhdl93.ast.interface_signal_declaration;

public abstract class SignalDeclaration
    extends TypedReferenceDeclaration {
  public SignalDeclaration(interface_signal_declaration node) {
    super(node.identifier_list.identifier, node.subtype_indication);
    Verify.verify(!node.identifier_list.nodeListOptional.present(), "List not flattened.");
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{reference=" + reference() + ", type=" + type() + '}';
  }
}
