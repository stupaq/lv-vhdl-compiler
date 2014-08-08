package stupaq.translation.vhdl2lv;

import com.google.common.base.Optional;

import stupaq.labview.hierarchy.Bundler;
import stupaq.labview.hierarchy.Control;
import stupaq.labview.hierarchy.ControlCluster;
import stupaq.labview.hierarchy.Indicator;
import stupaq.labview.hierarchy.IndicatorCluster;
import stupaq.labview.hierarchy.Unbundler;
import stupaq.labview.hierarchy.VI;
import stupaq.labview.scripting.tools.ControlStyle;
import stupaq.translation.naming.IOReference;
import stupaq.translation.naming.InstantiableName;
import stupaq.translation.project.LVProject;

import static com.google.common.base.Optional.of;
import static java.lang.String.valueOf;
import static stupaq.labview.scripting.tools.ConnectorPanePattern.DO_NOT_CONNECT;
import static stupaq.labview.scripting.tools.ControlStyle.NUMERIC_DBL;
import static stupaq.labview.scripting.tools.ControlStyle.NUMERIC_I32;
import static stupaq.translation.TranslationConventions.INPUTS_CONN_INDEX;
import static stupaq.translation.TranslationConventions.OUTPUTS_CONN_INDEX;
import static stupaq.translation.vhdl2lv.TranslationConventions.INPUTS_CONTROL;
import static stupaq.translation.vhdl2lv.TranslationConventions.OUTPUTS_CONTROL;

class UniversalVI {
  private final VI theVi;

  public VI createdVI() {
    return theVi;
  }

  public UniversalVI(LVProject project, InstantiableName lvName, InterfaceDeclaration entity,
      IOSources namedSources, IOSinks danglingSinks) {
    ConnPaneAllocator allocator = new ConnPaneAllocator(entity);
    theVi = new VI(project.tools(), project.allocate(lvName, true), allocator.pattern());
    if (allocator.isClustered()) {
      ControlCluster controlOwner = new ControlCluster(theVi, INPUTS_CONTROL, INPUTS_CONN_INDEX);
      IndicatorCluster indicatorOwner =
          new IndicatorCluster(theVi, OUTPUTS_CONTROL, OUTPUTS_CONN_INDEX);
      for (ConnectorPaneTerminal connector : entity.orderedTerminals()) {
        Optional<String> label = of(connector.representation());
        ControlStyle style = connector.isConstant() ? NUMERIC_I32 : NUMERIC_DBL;
        int ifaceIndex = connector.listIndex();
        if (connector.isInput()) {
          new Control(controlOwner, style, label, DO_NOT_CONNECT, valueOf(ifaceIndex));
        } else {
          new Indicator(indicatorOwner, style, label, DO_NOT_CONNECT, valueOf(ifaceIndex));
        }
      }
      Unbundler unbundler = new Unbundler(theVi, entity.inputs(), INPUTS_CONTROL);
      controlOwner.terminal().connectTo(unbundler.input(), Optional.<String>absent());
      Bundler bundler = new Bundler(theVi, entity.outputs(), OUTPUTS_CONTROL);
      bundler.output().connectTo(indicatorOwner.terminal(), Optional.<String>absent());
      int inputIndex = 0;
      int outputIndex = 0;
      for (ConnectorPaneTerminal connector : entity.orderedTerminals()) {
        IOReference ref = connector.reference();
        if (connector.isInput()) {
          namedSources.put(ref, unbundler.outputs().get(inputIndex++));
        } else {
          danglingSinks.put(ref, bundler.inputs().get(outputIndex++));
        }
      }
    } else {
      for (ConnectorPaneTerminal connector : entity.orderedTerminals()) {
        IOReference ref = connector.reference();
        Optional<String> label = of(connector.representation());
        ControlStyle style = connector.isConstant() ? NUMERIC_I32 : NUMERIC_DBL;
        String desc = valueOf(connector.listIndex());
        int connIndex = allocator.paneIndex(connector);
        if (connector.isInput()) {
          Control control = new Control(theVi, style, label, connIndex, desc);
          namedSources.put(ref, control.terminal());
        } else {
          Indicator indicator = new Indicator(theVi, style, label, connIndex, desc);
          danglingSinks.put(ref, indicator.terminal());
        }
      }
    }
  }
}
