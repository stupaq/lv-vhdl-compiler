package stupaq.translation.vhdl2lv;

import stupaq.translation.naming.IOReference;
import stupaq.translation.naming.TypeIndication;
import stupaq.vhdl93.ast.identifier;
import stupaq.vhdl93.ast.subtype_indication;

abstract class TypedReferenceDeclaration {
  private final IOReference reference;
  private final TypeIndication type;

  public TypedReferenceDeclaration(identifier id, subtype_indication subtype) {
    reference = new IOReference(id);
    type = new TypeIndication(subtype);
  }

  public final IOReference reference() {
    return reference;
  }

  public final TypeIndication type() {
    return type;
  }
}
