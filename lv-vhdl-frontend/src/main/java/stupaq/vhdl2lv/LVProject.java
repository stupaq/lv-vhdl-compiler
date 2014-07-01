package stupaq.vhdl2lv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import stupaq.labview.VIPath;
import stupaq.labview.scripting.ScriptingTools;
import stupaq.labview.scripting.hierarchy.VI;
import stupaq.vhdl93.ast.design_file;

public class LVProject {
  private final ScriptingTools tools;
  private final Path root;

  public LVProject(Path root) {
    this.root = root;
    tools = new ScriptingTools();
  }

  public void update(design_file n) {
    n.accept(new DesignFileEmitter(this));
  }

  public VI create(String name, boolean override) {
    VIPath path = resolve(name);
    if (override) {
      try {
        Files.deleteIfExists(path.path());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    VI vi = new VI(tools, path);
    vi.create();
    return vi;
  }

  public VIPath resolve(String name) {
    return new VIPath(root, name + ".vi");
  }
}
