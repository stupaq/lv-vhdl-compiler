package stupaq.parser;

import stupaq.vhdl93.VHDL93ParserConstants;

public class Symbol implements VHDL93ParserConstants {
  public String identifier;
  public int kind;

  public Symbol(String id, int k) {
    identifier = id;
    kind = k;
  }

  public void dump() {
    System.out.println("identifier: " + identifier);
    System.out.println("kind:       " + tokenImage[kind]);
  }
}


