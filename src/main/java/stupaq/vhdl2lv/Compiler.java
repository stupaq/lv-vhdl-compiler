package stupaq.vhdl2lv;

import stupaq.vhdl93.ErrorHandler;
import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.VHDL93Parser;

public class Compiler {
  public static void main(String args[]) throws Exception {
    if (args.length == 1) {
      java.io.FileInputStream file = new java.io.FileInputStream(args[0]);
      VHDL93Parser parser = new VHDL93Parser(file);
      parser.setupErrorHandler();
      try {
        System.out.println("reading from file: " + args[0]);
        parser.design_file();
        parser.errorManager.Summary();
      } catch (ParseException p) {
        System.out.println("syntax error: ");
        p.printStackTrace();
        throw p;
      } catch (Exception e) {
        System.out.println("error: ");
        e.printStackTrace();
        throw e;
      }
    } else {
      System.out.println("usage: Vhdl filename");
    }
  }
}
