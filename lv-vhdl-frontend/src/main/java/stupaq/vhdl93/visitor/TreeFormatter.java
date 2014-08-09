package stupaq.vhdl93.visitor;

import java.util.Enumeration;
import java.util.Vector;

import stupaq.vhdl93.ast.Node;
import stupaq.vhdl93.ast.NodeListInterface;
import stupaq.vhdl93.ast.NodeToken;

/**
 * A skeleton output formatter for your language grammar.  Using the add() method along with
 * force(), indent(), and outdent(), you can easily specify how this visitor will format the given
 * syntax tree. See the JTB documentation for more details.
 *
 * Pass your syntax tree to this visitor, and then to the TreeDumper visitor in order to "pretty
 * print" your tree.
 */
public class TreeFormatter extends DepthFirstVisitor {
  protected Vector<FormatCommand> cmdQueue = new Vector<>();
  private final boolean lineWrap;
  private final int wrapWidth;
  private final int indentAmt;
  private int curLine = 1;
  private int curColumn = 1;
  private int curIndent = 0;

  /**
   * The default constructor assumes an indentation amount of 3 spaces and no line-wrap.  You may
   * alternately use the other constructor to specify your own indentation amount and line width.
   */
  public TreeFormatter() {
    this(3, 0);
  }

  /**
   * This constructor accepts an indent amount and a line width which is used to wrap long lines. If
   * a token's beginColumn value is greater than the specified wrapWidth, it will be moved to the
   * next line and indented one extra level.  To turn off line-wrapping, specify a wrapWidth of 0.
   *
   * @param indentAmt Amount of spaces per indentation level.
   * @param wrapWidth Wrap lines longer than wrapWidth.  0 for no wrap.
   */
  public TreeFormatter(int indentAmt, int wrapWidth) {
    this.indentAmt = indentAmt;
    this.wrapWidth = wrapWidth;
    lineWrap = wrapWidth > 0;
  }

  /**
   * Accepts a NodeListInterface object and performs an optional format command between each node in
   * the list (but not after the last node).
   */
  protected void processList(NodeListInterface n) {
    processList(n, null);
  }

  protected void processList(NodeListInterface n, FormatCommand cmd) {
    for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      e.nextElement().accept(this);
      if (cmd != null && e.hasMoreElements()) {
        cmdQueue.addElement(cmd);
      }
    }
  }

  /**
   * A Force command inserts a line break and indents the next line to the current indentation
   * level.  Use "add(force());".
   */
  protected FormatCommand force() {
    return force(1);
  }

  protected FormatCommand force(int i) {
    return new FormatCommand(FormatCommand.FORCE, i);
  }

  /**
   * An Indent command increases the indentation level by one (or a user-specified amount).  Use
   * "add(indent());".
   */
  protected FormatCommand indent() {
    return indent(1);
  }

  protected FormatCommand indent(int i) {
    return new FormatCommand(FormatCommand.INDENT, i);
  }

  /**
   * An Outdent command is the reverse of the Indent command: it reduces the indentation level.  Use
   * "add(outdent());".
   */
  protected FormatCommand outdent() {
    return outdent(1);
  }

  protected FormatCommand outdent(int i) {
    return new FormatCommand(FormatCommand.OUTDENT, i);
  }

  /**
   * A Space command simply adds one or a user-specified number of spaces between tokens.  Use
   * "add(space());".
   */
  protected FormatCommand space() {
    return space(1);
  }

  protected FormatCommand space(int i) {
    return new FormatCommand(FormatCommand.SPACE, i);
  }

  /**
   * Use this method to add FormatCommands to the command queue to be executed when the next token
   * in the tree is visited.
   */
  protected void add(FormatCommand cmd) {
    cmdQueue.addElement(cmd);
  }

  /**
   * Executes the commands waiting in the command queue, then inserts the proper location
   * information into the current NodeToken.
   *
   * If there are any special tokens preceding this token, they will be given the current location
   * information.  The token will follow on the next line, at the proper indentation level.  If this
   * is not the behavior you want from special tokens, feel free to modify this method.
   */
  public void visit(NodeToken n) {
    for (Enumeration<FormatCommand> e = cmdQueue.elements(); e.hasMoreElements(); ) {
      FormatCommand cmd = e.nextElement();
      switch (cmd.getCommand()) {
        case FormatCommand.FORCE:
          curLine += cmd.getNumCommands();
          curColumn = curIndent + 1;
          break;
        case FormatCommand.INDENT:
          curIndent += indentAmt * cmd.getNumCommands();
          break;
        case FormatCommand.OUTDENT:
          if (curIndent >= indentAmt) {
            curIndent -= indentAmt * cmd.getNumCommands();
          }
          break;
        case FormatCommand.SPACE:
          curColumn += cmd.getNumCommands();
          break;
        default:
          throw new TreeFormatterException("Invalid value in command queue.");
      }
    }

    cmdQueue.removeAllElements();

    // Handle all special tokens preceding this NodeToken
    if (n.numSpecials() > 0) {
      for (Enumeration<NodeToken> e = n.specialTokens.elements(); e.hasMoreElements(); ) {
        NodeToken special = e.nextElement();

        // -Place the token.
        // -Move cursor to next line after the special token.
        // -Don't update curColumn--want to keep current indent level.
        placeToken(special, curLine, curColumn);
        curLine = special.endLine + 1;
      }
    }

    placeToken(n, curLine, curColumn);
    curLine = n.endLine;
    curColumn = n.endColumn;
  }

  /**
   * Inserts token location (beginLine, beginColumn, endLine, endColumn) information into the
   * NodeToken.  Takes into account line-wrap. Does not update curLine and curColumn.
   */
  private void placeToken(NodeToken n, int line, int column) {
    int length = n.tokenImage.length();

    // Find beginning of token.  Only line-wrap for single-line tokens
    if (!lineWrap || n.tokenImage.indexOf('\n') != -1 ||
        column + length <= wrapWidth) {
      n.beginColumn = column;
    } else {
      ++line;
      column = curIndent + indentAmt + 1;
      n.beginColumn = column;
    }

    n.beginLine = line;

    // Find end of token; don't count \n if it's the last character
    for (int i = 0; i < length; ++i) {
      if (n.tokenImage.charAt(i) == '\n' && i < length - 1) {
        ++line;
        column = 1;
      } else {
        ++column;
      }
    }

    n.endLine = line;
    n.endColumn = column;
  }
}

