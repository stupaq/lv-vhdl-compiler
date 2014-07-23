package stupaq.translation;

import java.io.PrintStream;

import stupaq.vhdl93.ParseException;

public final class ExceptionPrinter {
  private ExceptionPrinter() {
  }

  public static void print(Exception exception, PrintStream stream) {
    exception.printStackTrace();
    if (exception instanceof SemanticException) {
      stream.println("Translation error encountered:");
      stream.println(exception.getMessage());
    } else if (exception instanceof MissingFeatureException) {
      stream.println("Translation error encountered:");
      stream.println(exception.getMessage());
    } else if (exception instanceof ParseException) {
      stream.println("Translation error encountered:");
      stream.println(exception.getMessage());
    } else {
      exception.printStackTrace(stream);
    }
  }
}
