package stupaq.concepts;

import stupaq.naming.IOReference;

public interface ConnectorPaneTerminal {
  public boolean isInput();

  public boolean isConstant();

  public int connectorIndex();

  public void connectorIndex(int index);

  public IOReference reference();

  public String representation();
}
