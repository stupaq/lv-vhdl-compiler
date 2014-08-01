package stupaq.translation.vhdl2lv;

import com.google.common.base.Optional;
import com.google.common.base.Verify;

import stupaq.labview.hierarchy.Bundler;
import stupaq.labview.hierarchy.Control;
import stupaq.labview.hierarchy.ControlCluster;
import stupaq.labview.hierarchy.Indicator;
import stupaq.labview.hierarchy.IndicatorCluster;
import stupaq.labview.hierarchy.Unbundler;
import stupaq.labview.hierarchy.VI;
import stupaq.labview.scripting.tools.ConnectorPanePattern;
import stupaq.labview.scripting.tools.ControlStyle;
import stupaq.translation.naming.IOReference;
import stupaq.translation.naming.InstantiableName;
import stupaq.translation.project.LVProject;

import static com.google.common.base.Optional.of;
import static stupaq.translation.TranslationConventions.INPUTS_CONN_INDEX;
import static stupaq.translation.vhdl2lv.TranslationConventions.INPUTS_CONTROL;
import static stupaq.translation.TranslationConventions.OUTPUTS_CONN_INDEX;
import static stupaq.translation.vhdl2lv.TranslationConventions.OUTPUTS_CONTROL;
import static stupaq.labview.scripting.tools.ConnectorPanePattern.DO_NOT_CONNECT;
import static stupaq.labview.scripting.tools.ControlStyle.NUMERIC_DBL;
import static stupaq.labview.scripting.tools.ControlStyle.NUMERIC_I32;

class UniversalVI extends VI {
  private static final int CLUSTERED_VI_THRESHOLD = 26;

  public UniversalVI(LVProject project, InstantiableName lvName, InterfaceDeclaration entity,
      IOSources namedSources, IOSinks danglingSinks) {
    super(project.tools(), project.allocate(lvName, true),
        choosePattern(entity.inputs(), entity.outputs()));
    boolean clustered = isClusteredVI(entity.inputs(), entity.outputs());
    if (clustered) {
      ControlCluster controlOwner = new ControlCluster(this, INPUTS_CONTROL, INPUTS_CONN_INDEX);
      IndicatorCluster indicatorOwner =
          new IndicatorCluster(this, OUTPUTS_CONTROL, OUTPUTS_CONN_INDEX);
      for (ConnectorPaneTerminal connector : entity.allTerminals()) {
        Optional<String> label = of(connector.representation());
        ControlStyle style = connector.isConstant() ? NUMERIC_I32 : NUMERIC_DBL;
        int index = connector.connectorIndex();
        if (connector.isInput()) {
          new Control(controlOwner, style, label, DO_NOT_CONNECT, String.valueOf(index));
        } else {
          new Indicator(indicatorOwner, style, label, DO_NOT_CONNECT, String.valueOf(index));
        }
      }
      Unbundler unbundler = new Unbundler(this, entity.inputs(), INPUTS_CONTROL);
      controlOwner.terminal().connectTo(unbundler.input(), Optional.<String>absent());
      Bundler bundler = new Bundler(this, entity.outputs(), OUTPUTS_CONTROL);
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
        Verify.verify(index++ == connector.connectorIndex(), "All terminals given out of order.");
      }
    } else {
      for (ConnectorPaneTerminal connector : entity.allTerminals()) {
        IOReference ref = connector.reference();
        Optional<String> label = of(connector.representation());
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
