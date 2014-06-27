package stupaq.vhdl2lv;

import java.io.ByteArrayOutputStream;

import stupaq.vhdl93.ast.subtype_indication;
import stupaq.vhdl93.visitor.TreeDumper;
import stupaq.vhdl93.visitor.VHDLTreeFormatter;

public class TypeIndication extends VHDLElement<subtype_indication> {
  String identifier;

  public TypeIndication(subtype_indication node) {
    super(node);
    node.accept(new VHDLTreeFormatter());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    node.accept(new TreeDumper(baos));
    identifier = baos.toString();
  }

  public String identifier() {
    return identifier;
  }

  @Override
  public String toString() {
    return "TypeIndication{" + "identifier=" + identifier() + '}';
  }
}
