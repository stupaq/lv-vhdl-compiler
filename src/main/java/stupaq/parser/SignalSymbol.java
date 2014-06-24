package stupaq.parser;

import stupaq.vhdl93.ASTrelation;
import stupaq.vhdl93.VHDL93ParserConstants;

// FIXME unused
public class SignalSymbol extends Symbol {
  public Symbol type;
  public int signal_kind;
  public ASTrelation init;

  public SignalSymbol(String id, Symbol t, int sk, ASTrelation i) {
    super(id, VHDL93ParserConstants.SIGNAL);
    type = t;
    signal_kind = sk;
    init = i;
  }

  public SignalSymbol(String id, Symbol t, ASTrelation i) {
    super(id, VHDL93ParserConstants.VARIABLE);
    type = t;
    signal_kind = VHDL93ParserConstants.DEFAULT;
    init = i;
  }
}
