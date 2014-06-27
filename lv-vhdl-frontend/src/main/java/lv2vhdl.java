import java.io.FileInputStream;

import stupaq.vhdl93.VHDL93Parser;
import stupaq.vhdl93.ast.SimpleNode;
import stupaq.vhdl93.visitor.TreeDumper;

public class lv2vhdl {
  public static void main(String args[]) throws Exception {
    // FIXME this should got in opposite direction
    if (args.length == 1) {
      FileInputStream file = new FileInputStream(args[0]);
      VHDL93Parser parser = new VHDL93Parser(file);
      try {
        SimpleNode root = parser.design_file();
        System.out.println();
        System.out.println("pre-formatting");
        System.out.println("==============");
        TreeDumper dumper = new TreeDumper(System.out);
        root.accept(dumper);
        System.out.println();
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    } else {
      System.out.println("usage: filename");
    }
  }
}
