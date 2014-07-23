package stupaq.translation.naming;

import stupaq.vhdl93.ast.identifier;

import static stupaq.vhdl93.ast.Builders.choice;
import static stupaq.vhdl93.ast.Builders.token;

public class IOReference extends Identifier {
  private IOReference(String name) {
    super(name);
  }

  public IOReference(identifier n) {
    this(n.representation());
  }

  public identifier asIdentifier() {
    return new identifier(choice(token(toString())));
  }
}
