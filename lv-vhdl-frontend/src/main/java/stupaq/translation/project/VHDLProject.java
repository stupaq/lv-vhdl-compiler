package stupaq.translation.project;

import com.google.common.base.CharMatcher;
import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private static final Logger LOGGER = LoggerFactory.getLogger(VHDLProject.class);
  private static final CharMatcher FILE_FORBIDDEN_CHARS = CharMatcher.anyOf(".(");
  private static final CharMatcher FILE_REMOVE_CHARS = CharMatcher.anyOf(")");
  private static final CharSequence FILE_IDENTIFIER_SEPARATOR = "-";
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
    String original = lvName.elementName();
    if (original.contains(FILE_IDENTIFIER_SEPARATOR)) {
      LOGGER.warn("Element name contains identifier separator used in file names");
    }
    String name = FILE_REMOVE_CHARS.removeFrom(
        FILE_FORBIDDEN_CHARS.replaceFrom(original, FILE_IDENTIFIER_SEPARATOR));
    return root.resolve(name + ".vhd");
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
