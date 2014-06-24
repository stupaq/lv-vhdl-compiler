package stupaq.lv2vhdl;

import java.io.FileInputStream;
import java.io.PrintWriter;

import stupaq.parser.ErrorSummary;
import stupaq.vhdl93.VHDL93Parser;

public class LV2VHDL {
  public static void main(String args[]) throws Exception {
    // FIXME this should got in opposite direction
    if (args.length == 1) {
      FileInputStream file = new FileInputStream(args[0]);
      VHDL93Parser parser = new VHDL93Parser(file);
      parser.setErrorHandler(new ErrorSummary());
      try {
        System.out.println("reading from file: " + args[0]);
        parser.design_file();
        System.err.println(parser.getErrorHandler().summary());
        VHDLEmitterVisitor emitter = new VHDLEmitterVisitor(new PrintWriter(System.out));
        parser.rootNode().jjtAccept(emitter, null);
        emitter.flush();
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    } else {
      System.out.println("usage: filename");
    }
  }
}
