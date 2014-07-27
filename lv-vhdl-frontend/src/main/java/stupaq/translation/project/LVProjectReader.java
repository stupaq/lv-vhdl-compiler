package stupaq.translation.project;

import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import stupaq.labview.VIPath;
import stupaq.labview.scripting.ScriptingTools;
import stupaq.labview.scripting.activex.ActiveXScriptingTools;
import stupaq.labview.scripting.fake.FakeScriptingTools;

public class LVProjectReader implements Iterator<VIPath> {
  private static final Logger LOGGER = LoggerFactory.getLogger(LVProjectReader.class);
  private final ScriptingTools tools;
  private final Set<VIPath> done = Sets.newHashSet();
  private final List<VIPath> todo = Lists.newArrayList();

  public LVProjectReader(Iterable<VIPath> roots) {
    if (StandardSystemProperty.OS_NAME.value().toLowerCase().contains("windows")) {
      tools = new ActiveXScriptingTools();
    } else {
      tools = new FakeScriptingTools();
    }
    Iterables.addAll(todo, roots);
  }

  public ScriptingTools tools() {
    return tools;
  }

  public void addDependency(VIPath path) {
    if (!done.contains(path)) {
      todo.add(path);
    }
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
