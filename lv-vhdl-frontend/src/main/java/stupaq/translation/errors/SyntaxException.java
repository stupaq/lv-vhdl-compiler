package stupaq.translation.errors;

import stupaq.vhdl93.ParseException;

public class SyntaxException extends TranslationException {
  public SyntaxException(ParseException exception) {
    super(exception.getMessage(), exception.getCause());
  }
}
