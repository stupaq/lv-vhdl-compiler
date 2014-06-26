package stupaq.vhdl93.errors;

import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.Token;

public interface ErrorHandler {
  public void error(Token token, String message) throws ParseException;

  public String summary();
}
