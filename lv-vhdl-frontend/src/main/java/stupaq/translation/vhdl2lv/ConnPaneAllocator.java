package stupaq.translation.vhdl2lv;

import com.google.common.base.Verify;
import com.google.common.collect.ImmutableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import stupaq.labview.scripting.tools.ConnectorPanePattern;

import static stupaq.labview.scripting.tools.ConnectorPanePattern.*;

class ConnPaneAllocator {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConnPaneAllocator.class);
  private static final PatternMetadata CLUSTERED_PATTERN = P(P4801, L(0), L(1));
  private static final ImmutableList<PatternMetadata> PATTERN =
      ImmutableList.<PatternMetadata>builder()
          .add(P(P4800, L(0), L()))
          .add(P(P4800, L(), L(0)))
          .add(P(P4801, L(0), L(1)))
          .add(P(P4802, L(2, 1), L(0)))
          .add(P(P4805, L(3, 2), L(1, 0)))
          .add(P(P4807, L(4, 3, 2), L(1, 0)))
          .add(P(P4810, L(5, 4, 3), L(2, 1, 0)))
          .add(P(P4811, L(6, 5, 4, 3), L(2, 1, 0)))
          .add(P(P4812, L(7, 6, 5, 4), L(3, 2, 1, 0)))
          .add(P(P4813, L(4, 8, 7, 6, 5), L(3, 2, 1, 0)))
          .add(P(P4826, L(5, 9, 8, 7, 6), L(4, 3, 2, 1, 0)))
          .add(P(P4829, L(6, 10, 9, 8, 7, 5), L(4, 3, 2, 1, 0)))
          .add(P(P4815, L(7, 11, 10, 9, 8, 6), L(5, 3, 2, 1, 0, 4)))
          .add(P(P4833, L(1, 2, 0, 5, 7, 9, 11, 13, 12), L(3, 4, 6, 8, 10, 15, 14)))
          .add(P(P4834, L(6, 8, 0, 1, 2, 3, 4, 5, 9, 7), L(10, 12, 14, 15, 16, 17, 18, 19, 13, 11)))
          .add(P(P4835, L(15, 8, 10, 12, 0, 1, 2, 3, 4, 5, 6, 7, 13, 11, 9),
              L(14, 16, 18, 20, 21, 22, 23, 24, 25, 26, 27, 19, 17)))
          .build();
  private final boolean clustered;
  private final PatternMetadata choice;
  private final int inputsOffset, outputsOffset;

  public ConnPaneAllocator(InterfaceDeclaration entity) {
    int inputs = entity.inputs(),
        outputs = entity.outputs();
    PatternMetadata choice = null;
    for (PatternMetadata p : PATTERN) {
      if (p.canAccommodate(inputs, outputs)) {
        choice = p;
        break;
      }
    }
    clustered = choice == null;
    this.choice = clustered ? CLUSTERED_PATTERN : choice;
    Verify.verifyNotNull(this.choice);
    LOGGER.debug(
        clustered ? "Clustered VI" : "Unclustered VI, ConnPane pattern: " + choice.pattern);
    if (clustered) {
      inputsOffset = outputsOffset = 0;
    } else {
      inputsOffset = (choice.inputs.size() - inputs) / 2;
      outputsOffset = -inputs + (choice.outputs.size() - outputs) / 2;
    }
  }

  public int paneIndex(ConnectorPaneTerminal terminal) {
    int index = terminal.connectorIndex();
    if (clustered) {
      return index;
    }
    if (terminal.isInput()) {
      return choice.inputs.get(index + inputsOffset);
    } else {
      return choice.outputs.get(index + outputsOffset);
    }
  }

  public boolean isClustered() {
    return clustered;
  }

  public ConnectorPanePattern pattern() {
    return choice.pattern;
  }

  public static PatternMetadata P(ConnectorPanePattern pattern, List<Integer> inputs,
      List<Integer> outputs) {
    return new PatternMetadata(pattern, inputs, outputs);
  }

  public static List<Integer> L(Integer... args) {
    return Arrays.asList(args);
  }

  private static class PatternMetadata {
    private final ConnectorPanePattern pattern;
    private final List<Integer> inputs;
    private final List<Integer> outputs;

    public PatternMetadata(ConnectorPanePattern pattern, List<Integer> inputs,
        List<Integer> outputs) {
      this.pattern = pattern;
      this.inputs = inputs;
      this.outputs = outputs;
    }

    public boolean canAccommodate(int inputs, int outputs) {
      return inputs <= this.inputs.size() && outputs <= this.outputs.size();
    }
  }
}
