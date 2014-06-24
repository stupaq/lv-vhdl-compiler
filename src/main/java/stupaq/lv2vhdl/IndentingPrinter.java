package stupaq.lv2vhdl;

import java.io.PrintWriter;

public class IndentingPrinter {
  private int indentLevel;
  private String indent;
  private PrintWriter out;

  public IndentingPrinter() {
    this(new PrintWriter(System.out), "  ");
  }

  public IndentingPrinter(PrintWriter out, String indent) {
    this.out = out;
    this.indent = indent;
  }

  public void println() {
    out.println();
    printIndent();
  }

  public void println(Object o) {
    out.print(o);
    println();
  }

  public void print(Object o) {
    out.print(o);
  }

  public void increment() {
    ++indentLevel;
  }

  public void decrement() {
    --indentLevel;
  }

  private void printIndent() {
    for (int i = 0; i < indentLevel; i++) {
      out.print(indent);
    }
  }
}
