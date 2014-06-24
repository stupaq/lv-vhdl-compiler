package stupaq.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SymbolTable {
  public SymbolTable upperSymtab;
  List<Symbol> symbols = Collections.synchronizedList(new ArrayList<Symbol>());
  String blockName;

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
      return upperSymtab.getSymbol(identifier);
    } catch (Exception e) {
      return new Symbol("ERROR", 0);
    }
  }

  public void newBlock(String identifier) {
    blockName = identifier;
  }

  public void endBlock(String identifier) {
    if (!blockName.equals(identifier)) {
      System.out.println("ERROR: identifiers at start and end don't match");
    }
  }

  public void dump() {
    for (Symbol symbol : symbols) {
      symbol.dump();
    }
  }
}
