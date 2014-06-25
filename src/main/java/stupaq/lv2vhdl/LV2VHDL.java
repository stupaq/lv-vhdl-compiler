package stupaq.lv2vhdl;

import java.io.FileInputStream;
import java.io.PrintWriter;

import stupaq.parser.ErrorSummary;
import stupaq.vhdl93.VHDL93Parser;
import stupaq.vhdl93.ast.Node;
import stupaq.vhdl93.ast.SimpleNode;
import stupaq.vhdl93.visitor.TreeDumper;
import stupaq.vhdl93.visitor.TreeFormatter;

public class LV2VHDL {
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
        dumper.startAtNextToken();
        root.accept(dumper);
        System.out.println();
        System.out.println();
        System.out.println("post-formatting");
        System.out.println("===============");
        root.accept(new TreeFormatter(4, 120));
        dumper.resetPosition();
        root.accept(dumper);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    } else {
      System.out.println("usage: filename");
    }
  }
}
