package stupaq.concepts;

import stupaq.vhdl93.ast.SimpleNode;

abstract class VHDLElement<T extends SimpleNode> {
  private T node;

  protected VHDLElement(T node) {
    this.node = node;
  }

  public T node() {
    return node;
  }
}
