package stupaq.translation.vhdl2lv;

import stupaq.labview.scripting.tools.ConnectorPanePattern;

class ConnPaneAllocator {
  private static final int CLUSTERED_VI_THRESHOLD = 26;
  private final ConnectorPanePattern pattern;
  private final boolean clustered;

  public ConnPaneAllocator(InterfaceDeclaration entity) {
    pattern = choosePattern(entity.inputs(), entity.outputs());
    clustered = isClusteredVI(entity.inputs(), entity.outputs());
  }

  public int paneIndex(ConnectorPaneTerminal terminal) {
    return terminal.connectorIndex();
  }

  private static ConnectorPanePattern choosePattern(int inputs, int outputs) {
    final int connectorsCount = inputs + outputs;
    return isClusteredVI(inputs, outputs) ? ConnectorPanePattern.P4801
        : ConnectorPanePattern.choosePattern(connectorsCount);
  }

  public static boolean isClusteredVI(int inputs, int outputs) {
    final int connectorsCount = inputs + outputs;
    return connectorsCount > CLUSTERED_VI_THRESHOLD;
  }

  public boolean isClustered() {
    return clustered;
  }

  public ConnectorPanePattern pattern() {
    return pattern;
  }
}
