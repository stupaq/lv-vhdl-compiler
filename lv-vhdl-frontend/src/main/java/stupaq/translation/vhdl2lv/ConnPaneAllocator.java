package stupaq.translation.vhdl2lv;

import com.google.common.base.Verify;
import com.google.common.collect.ImmutableMap;

import java.util.List;

import stupaq.labview.scripting.tools.ConnectorPanePattern;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static stupaq.labview.scripting.tools.ConnectorPanePattern.*;

class ConnPaneAllocator {
  private static final int CLUSTERED_VI_THRESHOLD = 26;
  private static final ImmutableMap<ConnectorPanePattern, List<Integer>> patternToMapping =
      ImmutableMap.<ConnectorPanePattern, List<Integer>>builder()
          .put(P4800, singletonList(0))
          .put(P4801, asList(0, 1))
          .put(P4802, asList(0, 2, 1))
          .put(P4805, asList(1, 0, 3, 2))
          .put(P4807, asList(1, 0, 4, 3, 2))
          .put(P4810, asList(2, 1, 0, 5, 4, 3))
          .put(P4811, asList(2, 1, 0, 6, 5, 4, 3))
          .put(P4812, asList(3, 2, 1, 0, 7, 6, 5, 4))
          .put(P4813, asList(3, 2, 1, 0, 4, 8, 7, 6, 5))
          .put(P4826, asList(4, 3, 2, 1, 0, 5, 9, 8, 7, 6))
          .put(P4829, asList(4, 3, 2, 1, 0, 6, 10, 9, 8, 7, 5))
          .put(P4815, asList(5, 3, 2, 1, 0, 4, 7, 11, 10, 9, 8, 6))
          .put(P4833, asList(3, 4, 6, 8, 10, 15, 14, 1, 2, 0, 5, 7, 9, 11, 13, 12))
          .put(P4834, asList(12, 10, 14, 15, 16, 17, 18, 19, 11, 13, 6, 8, 0, 1, 2, 3, 4, 5, 9, 7))
          .put(P4835,
              asList(18, 16, 14, 20, 21, 22, 23, 24, 25, 26, 27, 15, 17, 19, 8, 10, 12, 0, 1, 2, 3,
                  4, 5, 6, 7, 13, 11, 9))
          .build();
  private final ConnectorPanePattern pattern;
  private final boolean clustered;
  private final List<Integer> order;

  public ConnPaneAllocator(InterfaceDeclaration entity) {
    int connectorsCount = entity.inputs() + entity.outputs();
    clustered = connectorsCount > CLUSTERED_VI_THRESHOLD;
    pattern = clustered ? ConnectorPanePattern.P4801
        : ConnectorPanePattern.choosePattern(connectorsCount);
    order = clustered ? null : patternToMapping.get(pattern);
    if (order != null) {
      Verify.verify(order.size() >= connectorsCount);
    }
  }

  public int paneIndex(ConnectorPaneTerminal terminal) {
    int index = terminal.connectorIndex();
    return order == null ? index : order.get(index);
  }

  public boolean isClustered() {
    return clustered;
  }

  public ConnectorPanePattern pattern() {
    return pattern;
  }
}
