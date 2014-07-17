package stupaq.vhdl2lv;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.util.List;

import stupaq.concepts.InterfaceDeclaration;
import stupaq.labview.VIErrorException;
import stupaq.labview.hierarchy.Bundler;
import stupaq.labview.hierarchy.Generic;
import stupaq.labview.hierarchy.SubVI;
import stupaq.labview.hierarchy.Terminal;
import stupaq.labview.hierarchy.Unbundler;
import stupaq.naming.InstantiableName;
import stupaq.project.LVProject;

import static com.google.common.base.Optional.of;
import static stupaq.vhdl2lv.UniversalVI.isClusteredVI;

class UniversalSubVI extends SubVI {
  private final List<Terminal> terminals = Lists.newArrayList();
  private final boolean clustered;

  public UniversalSubVI(Generic owner, LVProject project, InstantiableName lvName,
      InterfaceDeclaration entity, Optional<String> description) {
    super(owner, project.resolve(lvName), description);
    clustered = isClusteredVI(entity.inputs(), entity.outputs());
    if (clustered) {
      Bundler inputs = new Bundler(owner, entity.inputs(), of("inputs"));
      Unbundler outputs = new Unbundler(owner, entity.outputs(), of("outputs"));
      inputs.output().connectTo(super.terminal(1));
      super.terminal(0).connectTo(outputs.input());
      terminals.addAll(inputs.inputs());
      terminals.addAll(outputs.outputs());
    }
  }

  @Override
  public Terminal terminal(int index) {
    return clustered ? terminals.get(index) : super.terminal(index);
  }

  @Override
  public void delete() throws VIErrorException {
    super.delete();
  }
}
