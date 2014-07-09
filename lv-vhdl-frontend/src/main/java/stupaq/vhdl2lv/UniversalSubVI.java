package stupaq.vhdl2lv;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.util.List;

import stupaq.concepts.EntityDeclaration;
import stupaq.labview.VIErrorException;
import stupaq.labview.scripting.hierarchy.Bundler;
import stupaq.labview.scripting.hierarchy.Generic;
import stupaq.labview.scripting.hierarchy.SubVI;
import stupaq.labview.scripting.hierarchy.Terminal;
import stupaq.labview.scripting.hierarchy.Unbundler;
import stupaq.labview.scripting.hierarchy.Wire;

import static com.google.common.base.Optional.of;
import static stupaq.vhdl2lv.UniversalVI.isClusteredVI;

public class UniversalSubVI extends SubVI {
  private final List<Terminal> terminals = Lists.newArrayList();
  private final boolean clustered;

  public UniversalSubVI(Generic owner, LVProject project, EntityDeclaration entity,
      Optional<String> description) {
    super(owner, project.resolve(entity.name()), description);
    clustered = isClusteredVI(entity.inputs(), entity.outputs());
    if (clustered) {
      Bundler inputs = new Bundler(owner, entity.inputs(), of("inputs"));
      Unbundler outputs = new Unbundler(owner, entity.outputs(), of("outputs"));
      new Wire(owner, inputs.output(), super.terminal(0));
      new Wire(owner, super.terminal(1), outputs.input());
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
