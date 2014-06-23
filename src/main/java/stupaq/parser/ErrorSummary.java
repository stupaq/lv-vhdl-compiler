package stupaq.parser;

import stupaq.vhdl93.Token;

public class ErrorSummary implements ErrorHandler {
  public int Errors = 0;

  @Override
  public void error(Token token, String message) {
    System.err.println("line\t" + token.beginLine + ":\t" + message);
    Errors++;
  }

  @Override
  public String summary() {
    return "Syntax errors: " + Errors;
  }
}
