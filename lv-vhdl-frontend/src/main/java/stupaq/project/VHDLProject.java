package stupaq.project;

import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import stupaq.labview.VIPath;
import stupaq.labview.scripting.ScriptingTools;
import stupaq.labview.scripting.activex.ActiveXScriptingTools;
import stupaq.labview.scripting.fake.FakeScriptingTools;

public class VHDLProject implements Iterable<VIPath>, Iterator<VIPath> {
  private final ScriptingTools tools;
  private final Path root;
  private final Set<VIPath> done = Sets.newHashSet();
  private final List<VIPath> todo = Lists.newArrayList();

  public VHDLProject(Path root, Iterable<VIPath> roots) {
    this.root = root;
    if (StandardSystemProperty.OS_NAME.value().toLowerCase().contains("windows")) {
      tools = new ActiveXScriptingTools();
    } else {
      tools = new FakeScriptingTools();
    }
    Iterables.addAll(todo, roots);
  }

  public Path allocate(ProjectElementName name, boolean override) {
    Path path = resolve(name);
    if (override) {
      try {
        Files.deleteIfExists(path);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    try {
      Files.createDirectories(path.getParent());
    } catch (IOException ignored) {
    }
    return path;
  }

  public Path resolve(ProjectElementName lvName) {
    return root.resolve(lvName.elementName() + ".vhd");
  }

  public ScriptingTools tools() {
    return tools;
  }

  public void add(VIPath path) {
    if (!done.contains(path)) {
      todo.add(path);
    }
  }

  @Override
  public Iterator<VIPath> iterator() {
    return this;
  }

  @Override
  public synchronized boolean hasNext() {
    return !todo.isEmpty();
  }

  @Override
  public synchronized VIPath next() {
    VIPath next = todo.remove(0);
    if (next == null) {
      throw new NoSuchElementException();
    }
    done.add(next);
    return next;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
