package stupaq.vhdl2lv;

import stupaq.parser.ErrorSummary;
import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.VHDL93Parser;

public class Compiler {
  public static void main(String args[]) throws Exception {
    if (args.length == 1) {
      java.io.FileInputStream file = new java.io.FileInputStream(args[0]);
      VHDL93Parser parser = new VHDL93Parser(file);
      parser.errorHandler = new ErrorSummary();
      try {
        System.out.println("reading from file: " + args[0]);
        parser.design_file();
        System.err.println(parser.errorHandler.summary());
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    } else {
      System.out.println("usage: Vhdl filename");
    }
  }
}
