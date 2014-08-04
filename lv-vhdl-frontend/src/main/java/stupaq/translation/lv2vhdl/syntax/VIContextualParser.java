package stupaq.translation.lv2vhdl.syntax;

import com.ni.labview.VIDump;

import stupaq.labview.parsing.VIParser;

public final class VIContextualParser {
  private VIContextualParser() {
  }

  public static <E extends Exception> void visitVI(VIDump root, VIContextualVisitor<E> visitor)
      throws E {
    VIParser.visitVI(root, visitor);
  }
}
