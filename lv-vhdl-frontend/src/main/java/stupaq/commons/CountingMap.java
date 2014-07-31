package stupaq.commons;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

import java.util.Map;

public class CountingMap<T> extends ForwardingMap<T, Integer> {
  private final Map<T, Integer> delegate = Maps.newHashMap();

  @Override
  protected Map<T, Integer> delegate() {
    return delegate;
  }

  public int getDefault(T key) {
    Integer value = get(key);
    return value == null ? theDefault() : value;
  }

  private int theDefault() {
    return 0;
  }

  public void change(T key, int diff) {
    put(key, getDefault(key) + diff);
  }
}
