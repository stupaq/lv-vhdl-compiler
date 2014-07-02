package stupaq.concepts;

import stupaq.vhdl93.ast.identifier;

import static stupaq.vhdl93.ast.ASTGetters.representation;

public class IOReference extends Identifier {
  private IOReference(String name) {
    super(name);
  }

  public IOReference(identifier n) {
    this(representation(n));
  }
}
