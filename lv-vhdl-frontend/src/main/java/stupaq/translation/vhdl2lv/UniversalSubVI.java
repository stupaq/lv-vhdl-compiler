package stupaq.translation.vhdl2lv;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.util.List;

import stupaq.labview.hierarchy.Bundler;
import stupaq.labview.hierarchy.Generic;
import stupaq.labview.hierarchy.SubVI;
import stupaq.labview.hierarchy.Terminal;
import stupaq.labview.hierarchy.Unbundler;
import stupaq.translation.naming.InstantiableName;
import stupaq.translation.project.LVProject;

import static stupaq.translation.TranslationConventions.INPUTS_CONN_INDEX;
import static stupaq.translation.TranslationConventions.OUTPUTS_CONN_INDEX;
import static stupaq.translation.vhdl2lv.TranslationConventions.INPUTS_CONTROL;
import static stupaq.translation.vhdl2lv.TranslationConventions.OUTPUTS_CONTROL;

class UniversalSubVI {
  private final SubVI theSubVI;
  private final ConnPaneAllocator allocator;
  private final List<Terminal> terminals = Lists.newArrayList();

  public UniversalSubVI(Generic owner, LVProject project, InstantiableName lvName,
      InterfaceDeclaration entity, Optional<String> description) {
    theSubVI = new SubVI(owner, project.resolve(lvName), description);
    allocator = new ConnPaneAllocator(entity);
    if (allocator.isClustered()) {
      Bundler inputs = new Bundler(owner, entity.inputs(), INPUTS_CONTROL);
      Unbundler outputs = new Unbundler(owner, entity.outputs(), OUTPUTS_CONTROL);
      inputs.output().connectTo(theSubVI.terminal(INPUTS_CONN_INDEX));
      theSubVI.terminal(OUTPUTS_CONN_INDEX).connectTo(outputs.input());
      terminals.addAll(inputs.inputs());
      terminals.addAll(outputs.outputs());
    }
  }

  public Terminal terminal(ConnectorPaneTerminal terminal) {
    if (allocator.isClustered()) {
      return terminals.get(terminal.connectorIndex());
    } else {
      return theSubVI.terminal(allocator.paneIndex(terminal));
    }
  }
}
