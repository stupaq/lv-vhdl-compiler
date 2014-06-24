package stupaq.parser;

import stupaq.vhdl93.ASTarchitecture_body;
import stupaq.vhdl93.ASTentity_declaration;
import stupaq.vhdl93.ASTidentifier;
import stupaq.vhdl93.ASTprocess_statement;
import stupaq.vhdl93.ParseException;
import stupaq.vhdl93.SimpleNode;
import stupaq.vhdl93.VHDL93Parser;

public class ContextSensitiveCheckVisitor extends NoOpVisitor<Void, VHDL93Parser, ParseException> {
  @Override
  public Void visit(ASTarchitecture_body node, VHDL93Parser parser) throws ParseException {
    return checkMatchingIdentifiers(node, parser);
  }

  @Override
  public Void visit(ASTentity_declaration node, VHDL93Parser parser) throws ParseException {
    return checkMatchingIdentifiers(node, parser);
  }

  @Override
  public Void visit(ASTprocess_statement node, VHDL93Parser parser) throws ParseException {
    return checkMatchingIdentifiers(node, parser);
  }

  private Void checkMatchingIdentifiers(SimpleNode node, VHDL93Parser parser)
      throws ParseException {
    ASTidentifier id1 = (ASTidentifier) node.jjtGetChild(0);
    ASTidentifier id2 = (ASTidentifier) node.jjtGetChild(node.jjtGetNumChildren() - 1);
    if (id2.toString().equals("identifier")) {
      String s1 = id1.name;
      String s2 = id2.name;
      if (!s1.equals(s2)) {
        parser.getErrorHandler()
            .error(id2.jjtGetFirstToken(), "identifiers don't match: " + s1 + "/=" + s2);
      }
    }
    return null;
  }
}
