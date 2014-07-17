package stupaq;

import java.io.PrintStream;

import stupaq.vhdl93.ParseException;

public class ExceptionPrinter {
  private ExceptionPrinter() {
  }

  public static void print(Exception exception, PrintStream strem) {
    if (exception instanceof SemanticException) {
      strem.println(exception.getMessage());
    } else if (exception instanceof MissingFeatureException) {
      strem.println(exception.getMessage());
    } else if (exception instanceof ParseException) {
      strem.println(exception.getMessage());
    } else {
      exception.printStackTrace(strem);
    }
  }
}
