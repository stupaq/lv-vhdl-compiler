package stupaq.vhdl93;

public class ErrorHandler {
  public VHDL93Parser parser;
  public int Level1Warnings = 0;
  public int Errors = 0;

  public ErrorHandler(VHDL93Parser parser) {
    this.parser = parser;
  }

  public void WarnLevel1(String w) {
    Token t = parser.getToken(0);
    System.err.println("line " + t.beginLine + ": " + w + " in SIWG Level 1");
    Level1Warnings++;
  }

  public void Error(String w) {
    Token t = parser.getToken(0);
    System.err.println("line " + t.beginLine + ": " + w);
    Errors++;
  }

  public void Summary() {
    System.err.println("Incompatibilities with SIWG Level 1 Subset: " + Level1Warnings);
    System.err.println("Syntax errors: " + Errors);
  }
}
