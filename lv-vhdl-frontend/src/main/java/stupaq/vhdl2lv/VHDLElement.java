package stupaq.vhdl2lv;

import stupaq.vhdl93.ast.SimpleNode;

public abstract class VHDLElement<T extends SimpleNode> {
  private T node;

  protected VHDLElement(T node) {
    this.node = node;
  }

  public T node() {
    return node;
  }
}
