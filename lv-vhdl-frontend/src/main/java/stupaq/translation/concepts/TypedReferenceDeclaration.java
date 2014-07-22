package stupaq.translation.concepts;

import stupaq.translation.naming.IOReference;
import stupaq.vhdl93.ast.identifier;
import stupaq.vhdl93.ast.subtype_indication;

abstract class TypedReferenceDeclaration {
  private final TypeIndication type;
  private final IOReference reference;

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
