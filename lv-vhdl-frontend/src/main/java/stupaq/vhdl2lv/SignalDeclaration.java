package stupaq.vhdl2lv;

import com.google.common.base.Verify;

import stupaq.vhdl93.ast.interface_signal_declaration;

public class SignalDeclaration extends TypedReferenceDeclaration<interface_signal_declaration> {
  public SignalDeclaration(interface_signal_declaration node) {
    super(node, node.identifier_list.identifier, node.subtype_indication);
    Verify.verify(!node.identifier_list.nodeListOptional.present());
  }

  @Override
  public String toString() {
    return "SignalDeclaration{" + "reference=" + reference + ", type=" + type + '}';
  }
}
