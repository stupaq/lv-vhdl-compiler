package stupaq.vhdl93.ast;

import com.google.common.base.Verify;

import stupaq.vhdl93.visitor.GJNoArguDepthFirst;

public class PropertyAccessor {
  public static String representation(identifier n) {
    return n.nodeChoice.accept(new GJNoArguDepthFirst<String>() {
      @Override
      public String visit(NodeToken n) {
        return n.tokenImage.toLowerCase();
      }
    });
  }

  public static String representation(identifier_list n) {
    Verify.verify(!n.nodeListOptional.present());
    return representation(n.identifier);
  }
}
