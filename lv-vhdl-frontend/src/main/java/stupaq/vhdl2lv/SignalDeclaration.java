package stupaq.vhdl2lv;

import com.google.common.base.Verify;

import stupaq.vhdl93.ast.interface_signal_declaration;

public class SignalDeclaration extends HDLElement<interface_signal_declaration> {
  public final TypeIndication type;
  private final IOReference reference;

  public SignalDeclaration(interface_signal_declaration node) {
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
    return "SignalDeclaration{" + "reference=" + reference + ", type=" + type + '}';
  }
}
