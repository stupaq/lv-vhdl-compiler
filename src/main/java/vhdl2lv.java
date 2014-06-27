import java.io.FileInputStream;

import stupaq.labview.scripting.ScriptingTools;
import stupaq.vhdl2lv.LVTranslationVisitor;
import stupaq.vhdl93.VHDL93Parser;
import stupaq.vhdl93.ast.SimpleNode;

public class vhdl2lv {
  public static void main(String args[]) throws Exception {
    if (args.length == 1) {
      FileInputStream file = new FileInputStream(args[0]);
      VHDL93Parser parser = new VHDL93Parser(file);
      try {
        SimpleNode root = parser.design_file();
        ScriptingTools tools = null;  // FIXME new ScriptingTools();
        LVTranslationVisitor visitor = new LVTranslationVisitor(tools);
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
