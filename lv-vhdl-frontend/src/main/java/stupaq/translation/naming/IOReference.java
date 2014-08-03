package stupaq.translation.naming;

import com.google.common.base.VerifyException;

import java.io.StringReader;

import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.VHDL93Parser;
import stupaq.vhdl93.VHDL93ParserTotal;
import stupaq.vhdl93.ast.identifier;

public class IOReference extends Identifier {
  private IOReference(String name) {
    super(name);
  }

  public IOReference(identifier n) {
    this(n.representation());
  }

  public identifier asIdentifier() {
    try {
      return new VHDL93ParserTotal(new StringReader(toString())).identifier();
    } catch (ParseException e) {
      // This can not happen.
      throw new VerifyException("Cannot parse reference as an identifier.");
    }
  }
}
