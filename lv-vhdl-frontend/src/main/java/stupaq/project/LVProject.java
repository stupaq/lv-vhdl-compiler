package stupaq.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import stupaq.labview.VIPath;
import stupaq.labview.scripting.ScriptingTools;
import stupaq.labview.scripting.activex.ActiveXScriptingTools;

public class LVProject {
  private final ScriptingTools tools;
  private final Path root;

  public LVProject(Path root) {
    this.root = root;
    tools = new ActiveXScriptingTools();
  }

  public VIPath allocate(ProjectElementName name, boolean override) {
    VIPath path = resolve(name);
    if (override) {
      try {
        Files.deleteIfExists(path.path());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    try {
      Files.createDirectories(path.path().getParent());
    } catch (IOException ignored) {
    }
    return path;
  }

  public VIPath resolve(ProjectElementName lvName) {
    return new VIPath(root, lvName.elementName() + ".vi");
  }

  public ScriptingTools tools() {
    return tools;
  }
}
