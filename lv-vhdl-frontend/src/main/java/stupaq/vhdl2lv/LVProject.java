package stupaq.vhdl2lv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import stupaq.labview.VIPath;
import stupaq.labview.scripting.EditableVI;
import stupaq.labview.scripting.ScriptingTools;

public class LVProject {
  private final ScriptingTools tools;
  private final Path root;

  public LVProject(Path root) {
    this.root = root;
    tools = new ScriptingTools();
  }

  public EditableVI create(String name, boolean override) {
    VIPath path = new VIPath(root, name + ".vi");
    if (override) {
      try {
        Files.deleteIfExists(path.path());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    EditableVI vi = new EditableVI(tools, path);
    vi.create();
    return vi;
  }
}
