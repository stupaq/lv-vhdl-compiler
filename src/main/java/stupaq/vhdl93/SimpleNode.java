/* Generated By:JJTree: Do not edit this line. SimpleNode.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package stupaq.vhdl93;

public
class SimpleNode implements Node {

  protected SimpleNode parent;
  protected SimpleNode[] children;
  protected int id;
  protected Object value;
  protected VHDL93Parser parser;
  protected Token firstToken;
  protected Token lastToken;

  public SimpleNode(int i) {
    id = i;
  }

  public SimpleNode(VHDL93Parser p, int i) {
    this(i);
    parser = p;
  }

  public void jjtOpen() {
  }

  public void jjtClose() {
  }

  public void jjtSetParent(Node n) { parent = (SimpleNode) n; }
  public SimpleNode jjtGetParent() { return parent; }

  public void jjtAddChild(Node n, int i) {
    if (children == null) {
      children = new SimpleNode[i + 1];
    } else if (i >= children.length) {
      SimpleNode c[] = new SimpleNode[i + 1];
      System.arraycopy(children, 0, c, 0, children.length);
      children = c;
    }
    children[i] = (SimpleNode) n;
  }

  public SimpleNode jjtGetChild(int i) {
    return children[i];
  }

  public int jjtGetNumChildren() {
    return (children == null) ? 0 : children.length;
  }

  public void jjtSetValue(Object value) { this.value = value; }
  public Object jjtGetValue() { return value; }

  public Token jjtGetFirstToken() { return firstToken; }
  public void jjtSetFirstToken(Token token) { this.firstToken = token; }
  public Token jjtGetLastToken() { return lastToken; }
  public void jjtSetLastToken(Token token) { this.lastToken = token; }

  /** Accept the visitor. **/
  public <Result, Argument, Failure extends Exception> Result jjtAccept(VHDL93ParserVisitor<Result, Argument, Failure> visitor, Argument data) throws Failure
  {
    return visitor.visit(this, data);
  }

  /** Accept the visitor. **/
  public <Argument, Failure extends Exception> void childrenAccept(VHDL93ParserVisitor<Void, Argument, Failure> visitor, Argument data) throws Failure
  {
    if (children != null) {
      for (SimpleNode aChildren : children) {
        aChildren.jjtAccept(visitor, data);
      }
    }
  }

  /* You can override these two methods in subclasses of SimpleNode to
     customize the way the node appears when the tree is dumped.  If
     your output uses more than one line you should override
     toString(String), otherwise overriding toString() is probably all
     you need to do. */

  public String toString() { return VHDL93ParserTreeConstants.jjtNodeName[id]; }
  public String toString(String prefix) { return prefix + toString(); }

  /* Override this method if you want to customize how the node dumps
     out its children. */

  public void dump(String prefix) {
    System.out.println(toString(prefix));
    if (children != null) {
      for (SimpleNode aChildren : children) {
        if (aChildren != null) {
          ((SimpleNode) aChildren).dump(prefix + " ");
        }
      }
    }
  }
}

/* JavaCC - OriginalChecksum=476fb61a734849738f8bfc6c5a1feecb (do not edit this line) */