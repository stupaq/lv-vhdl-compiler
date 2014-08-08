package stupaq.translation.vhdl2lv;

import stupaq.translation.naming.IOReference;

interface ConnectorPaneTerminal {
  public boolean isInput();

  public boolean isConstant();

  public int listIndex();

  public int connectorIndex();

  public void connectorIndex(int index);

  public IOReference reference();

  public String representation();
}
