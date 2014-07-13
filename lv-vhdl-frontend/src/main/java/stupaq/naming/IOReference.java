package stupaq.naming;

import stupaq.naming.Identifier;
import stupaq.vhdl93.ast.identifier;

public class IOReference extends Identifier {
  private IOReference(String name) {
    super(name);
  }

  public IOReference(identifier n) {
    this(n.representation());
  }
}
