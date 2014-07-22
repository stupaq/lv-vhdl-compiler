package stupaq.vhdl93.visitor;

public class FormatCommand {
  public static final int FORCE = 0;
  public static final int INDENT = 1;
  public static final int OUTDENT = 2;
  public static final int SPACE = 3;

  private int command;
  private int numCommands;

  FormatCommand(int command, int numCommands) {
    this.command = command;
    this.numCommands = numCommands;
  }

  public int getCommand() {
    return command;
  }

  public int getNumCommands() {
    return numCommands;
  }

  public void setCommand(int i) {
    command = i;
  }

  public void setNumCommands(int i) {
    numCommands = i;
  }
}
