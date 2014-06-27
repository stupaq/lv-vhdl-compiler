package stupaq.vhdl2lv;

import stupaq.types.Type;
import stupaq.vhdl93.ast.subtype_indication;
import stupaq.vhdl93.visitor.GJNoArguDepthFirst;

public class TypeIndication extends VHDLElement<subtype_indication> {
  Type type;

  public TypeIndication(subtype_indication node) {
    super(node);
    type = node.nodeChoice.accept(new GJNoArguDepthFirst<Type>() {
      // FIXME
    });
  }

  @Override
  public String toString() {
    return "TypeIndication{" + "type=" + type + '}';
  }
}
