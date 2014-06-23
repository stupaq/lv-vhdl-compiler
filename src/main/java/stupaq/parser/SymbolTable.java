package stupaq.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SymbolTable {
  public SymbolTable upper_symtab;
  List<Symbol> symbols = Collections.synchronizedList(new ArrayList<Symbol>());
  String block_name;

  public void addSymbol(Symbol s) {
    symbols.add(s);
  }

  public Symbol getSymbol(String identifier) {
    for (Symbol symbol : symbols) {
      if (identifier.compareTo(symbol.identifier) == 0) {
        return symbol;
      }
    }
    try {
      return upper_symtab.getSymbol(identifier);
    } catch (Exception e) {
      return new Symbol("ERROR", 0);
    }
  }

  public void newBlock(String identifier) {
    block_name = identifier;
  }

  public void endBlock(String identifier) {
    if (!block_name.equals(identifier)) {
      System.out.println("ERROR: identifiers at start and end don't match");
    }
  }

  public void dump() {
    for (Symbol symbol : symbols) {
      symbol.dump();
    }
  }
}
