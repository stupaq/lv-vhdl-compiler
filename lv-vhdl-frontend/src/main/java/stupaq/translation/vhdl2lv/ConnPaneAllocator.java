package stupaq.translation.vhdl2lv;

import stupaq.labview.scripting.tools.ConnectorPanePattern;

class ConnPaneAllocator {
  private static final int CLUSTERED_VI_THRESHOLD = 26;
  private final ConnectorPanePattern pattern;
  private final boolean clustered;
  private final int connectorsCount;

  public ConnPaneAllocator(InterfaceDeclaration entity) {
    connectorsCount = entity.inputs() + entity.outputs();
    clustered = connectorsCount > CLUSTERED_VI_THRESHOLD;
    pattern = clustered ? ConnectorPanePattern.P4801
        : ConnectorPanePattern.choosePattern(connectorsCount);
  }

  public int paneIndex(ConnectorPaneTerminal terminal) {
    return connectorsCount - 1 - terminal.connectorIndex();
  }

  public boolean isClustered() {
    return clustered;
  }

  public ConnectorPanePattern pattern() {
    return pattern;
  }
}
