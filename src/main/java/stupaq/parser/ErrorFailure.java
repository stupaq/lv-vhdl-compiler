package stupaq.parser;

import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.Token;

public class ErrorFailure implements ErrorHandler {
  @Override
  public void error(Token token, String w) throws ParseException {
    throw new ParseException("line\t" + token.beginLine + ":\t" + w);
  }

  @Override
  public String summary() {
    return null;
  }
}
