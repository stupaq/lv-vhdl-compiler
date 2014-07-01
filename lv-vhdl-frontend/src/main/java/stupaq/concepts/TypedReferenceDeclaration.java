package stupaq.concepts;

import stupaq.vhdl93.ast.SimpleNode;
import stupaq.vhdl93.ast.identifier;
import stupaq.vhdl93.ast.subtype_indication;

abstract class TypedReferenceDeclaration<T extends SimpleNode> extends VHDLElement<T> {
  public final TypeIndication type;
  protected final IOReference reference;

  public TypedReferenceDeclaration(T node, identifier id, subtype_indication subtype) {
    super(node);
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
