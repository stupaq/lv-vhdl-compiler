package stupaq.vhdl2lv;

import stupaq.vhdl93.ast.SimpleNode;

public abstract class HDLElement<T extends SimpleNode> {
  private T node;

  protected HDLElement(T node) {
    this.node = node;
  }

  public T node() {
    return node;
  }
}
