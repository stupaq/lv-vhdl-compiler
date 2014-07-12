package stupaq.vhdl2lv;

import com.google.common.base.Optional;
import com.google.common.base.Verify;

import stupaq.concepts.EntityDeclaration;
import stupaq.concepts.IOReference;
import stupaq.labview.scripting.hierarchy.Bundler;
import stupaq.labview.scripting.hierarchy.Control;
import stupaq.labview.scripting.hierarchy.ControlCluster;
import stupaq.labview.scripting.hierarchy.Indicator;
import stupaq.labview.scripting.hierarchy.IndicatorCluster;
import stupaq.labview.scripting.hierarchy.Unbundler;
import stupaq.labview.scripting.hierarchy.VI;
import stupaq.labview.scripting.tools.ConnectorPanePattern;
import stupaq.labview.scripting.tools.ControlStyle;
import stupaq.metadata.ConnectorPaneTerminal;

import static com.google.common.base.Optional.of;
import static stupaq.labview.scripting.tools.ControlCreate.DO_NOT_CONNECT;
import static stupaq.labview.scripting.tools.ControlStyle.NUMERIC_DBL;
import static stupaq.labview.scripting.tools.ControlStyle.NUMERIC_I32;

public class UniversalVI extends VI {
  private static final int CLUSTERED_VI_THRESHOLD = 26;

  public UniversalVI(LVProject project, EntityDeclaration entity, IOSources namedSources,
      IOSinks danglingSinks) {
    super(project.tools(), project.allocate(entity.name(), true),
        choosePattern(entity.inputs(), entity.outputs()));
    boolean clustered = isClusteredVI(entity.inputs(), entity.outputs());
    if (clustered) {
      ControlCluster controlOwner = new ControlCluster(this, of("inputs"), 1);
      IndicatorCluster indicatorOwner = new IndicatorCluster(this, of("outputs"), 0);
      for (ConnectorPaneTerminal connector : entity.allTerminals()) {
        IOReference ref = connector.reference();
        Optional<String> label = of(ref.toString());
        ControlStyle style = connector.isConstant() ? NUMERIC_I32 : NUMERIC_DBL;
        if (connector.isInput()) {
          new Control(controlOwner, style, label, DO_NOT_CONNECT);
        } else {
          new Indicator(indicatorOwner, style, label, DO_NOT_CONNECT);
        }
      }
      Unbundler unbundler = new Unbundler(this, entity.inputs(), of("inputs"));
      controlOwner.terminal().connectTo(unbundler.input(), Optional.<String>absent());
      Bundler bundler = new Bundler(this, entity.outputs(), of("outputs"));
      bundler.output().connectTo(indicatorOwner.terminal(), Optional.<String>absent());
      int index = 0;
      int inputIndex = 0;
      int outputIndex = 0;
      for (ConnectorPaneTerminal connector : entity.allTerminals()) {
        IOReference ref = connector.reference();
        if (connector.isInput()) {
          namedSources.put(ref, unbundler.outputs().get(inputIndex++));
        } else {
          danglingSinks.put(ref, bundler.inputs().get(outputIndex++));
        }
        Verify.verify(index++ == connector.connectorIndex());
      }
    } else {
      for (ConnectorPaneTerminal connector : entity.allTerminals()) {
        IOReference ref = connector.reference();
        Optional<String> label = of(ref.toString());
        ControlStyle style = connector.isConstant() ? NUMERIC_I32 : NUMERIC_DBL;
        int index = connector.connectorIndex();
        if (connector.isInput()) {
          namedSources.put(ref, new Control(this, style, label, index).terminal());
        } else {
          danglingSinks.put(ref, new Indicator(this, style, label, index).terminal());
        }
      }
    }
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
}
