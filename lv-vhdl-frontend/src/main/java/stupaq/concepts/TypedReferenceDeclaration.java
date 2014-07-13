package stupaq.concepts;

import stupaq.naming.IOReference;
import stupaq.vhdl93.ast.SimpleNode;
import stupaq.vhdl93.ast.identifier;
import stupaq.vhdl93.ast.subtype_indication;

abstract class TypedReferenceDeclaration<T extends SimpleNode> {
  private final TypeIndication type;
  private final IOReference reference;

  public TypedReferenceDeclaration(T node, identifier id, subtype_indication subtype) {
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
