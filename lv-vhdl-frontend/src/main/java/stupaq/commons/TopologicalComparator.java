package stupaq.commons;

import com.google.common.base.Suppliers;
import com.google.common.base.Verify;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;

import java.util.Comparator;
import java.util.Queue;

public class TopologicalComparator<T> implements Comparator<T> {
  private final MapWithDefault<T, Integer> topoIndices;

  public TopologicalComparator(Multimap<T, T> edges, Iterable<T> vertices, boolean missingFirst) {
    topoIndices = new MapWithDefault<>(Maps.<T, Integer>newHashMap(),
        Suppliers.ofInstance(missingFirst ? 0 : Integer.MAX_VALUE));
    MapWithDefault<T, Integer> inDegrees =
        new MapWithDefault<>(Maps.<T, Integer>newHashMap(), Suppliers.ofInstance(0));
    for (T v : edges.values()) {
      inDegrees.put(v, inDegrees.getDefault(v) + 1);
    }
    Queue<T> queue = Queues.newArrayDeque();
    for (T v : vertices) {
      if (inDegrees.getDefault(v) == 0) {
        queue.add(v);
      }
    }
    int nextIndex = 0;
    while (!queue.isEmpty()) {
      T v = queue.poll();
      Verify.verify(!topoIndices.containsKey(v));
      topoIndices.put(v, ++nextIndex);
      for (T u : edges.get(v)) {
        inDegrees.put(u, inDegrees.getDefault(u) - 1);
        if (inDegrees.getDefault(u) == 0) {
          queue.add(u);
        }
      }
    }
  }

  @Override
  public int compare(T o1, T o2) {
    return topoIndices.getDefault(o1).compareTo(topoIndices.getDefault(o2));
  }
}
