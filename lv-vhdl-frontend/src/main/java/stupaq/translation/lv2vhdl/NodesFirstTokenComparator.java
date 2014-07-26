package stupaq.translation.lv2vhdl;

import java.util.Comparator;

import stupaq.vhdl93.ast.Node;
import stupaq.vhdl93.ast.NodeToken;
import stupaq.vhdl93.visitor.DepthFirstVisitor;

import static stupaq.vhdl93.VHDL93ParserConstants.EOF;
import static stupaq.vhdl93.ast.Builders.token;

class NodesFirstTokenComparator implements Comparator<Node> {
  @Override
  public int compare(Node o1, Node o2) {
    return new TokenFindingVisitor().firstToken(o1).tokenImage.compareTo(
        new TokenFindingVisitor().firstToken(o2).tokenImage);
  }

  private static class TokenFindingVisitor extends DepthFirstVisitor {
    public NodeToken firstToken(Node n) {
      try {
        n.accept(this);
      } catch (TokenFoundException e) {
        return e.getToken();
      }
      return token(EOF);
    }

    @Override
    public void visit(NodeToken n) {
      throw new TokenFoundException(n);
    }
  }

  private static class TokenFoundException extends RuntimeException {
    private final NodeToken token;

    private TokenFoundException(NodeToken token) {
      this.token = token;
    }

    public NodeToken getToken() {
      return token;
    }
  }
}
