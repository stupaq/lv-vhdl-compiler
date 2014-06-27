import java.io.FileInputStream;
import java.nio.file.Paths;

import stupaq.vhdl2lv.LVProject;
import stupaq.vhdl2lv.LVTranslationVisitor;
import stupaq.vhdl93.VHDL93Parser;
import stupaq.vhdl93.ast.SimpleNode;

public class vhdl2lv {
  public static void main(String args[]) throws Exception {
    if (args.length == 2) {
      FileInputStream file = new FileInputStream(args[0]);
      VHDL93Parser parser = new VHDL93Parser(file);
      try {
        SimpleNode root = parser.design_file();
        LVProject project = new LVProject(Paths.get(args[1]));
        LVTranslationVisitor visitor = new LVTranslationVisitor(project);
        root.accept(visitor);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    } else {
      System.out.println("usage: filename");
    }
  }
}
