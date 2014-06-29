package stupaq.vhdl2lv;

import com.google.common.collect.ForwardingObject;

import stupaq.vhdl93.ast.identifier;

import static stupaq.vhdl93.ast.ASTGetters.representation;

public class IOReference extends ForwardingObject {
  private final String name;

  public IOReference(String name) {
    this.name = name;
  }

  @Override
  protected Object delegate() {
    return name;
  }

  public static IOReference from(identifier n) {
    return new IOReference(representation(n));
  }
}
