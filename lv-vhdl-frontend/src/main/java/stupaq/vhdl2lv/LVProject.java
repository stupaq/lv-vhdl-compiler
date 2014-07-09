package stupaq.vhdl2lv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import stupaq.concepts.EntityName;
import stupaq.labview.VIPath;
import stupaq.labview.scripting.ScriptingTools;
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

  public VIPath resolve(EntityName name) {
    return new VIPath(root, name + ".vi");
  }

  public VIPath allocate(EntityName name, boolean override) {
    VIPath path = resolve(name);
    if (override) {
      try {
        Files.deleteIfExists(path.path());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return path;
  }

  public ScriptingTools tools() {
    return tools;
  }
}
