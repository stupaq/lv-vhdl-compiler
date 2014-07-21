package stupaq.commons;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

import java.util.Map;

public class IntegerMap<T> extends ForwardingMap<Integer, T> {
  private final Map<Integer, T> delegate = Maps.newTreeMap();

  @Override
  protected Map<Integer, T> delegate() {
    return delegate;
  }

  @Override
  public T put(Integer key, T value) {
    Preconditions.checkNotNull(key);
    return super.put(key, value);
  }

  public T getPresent(Object key) {
    Preconditions.checkState(containsKey(key), "Missing value for key: %s.", key);
    return get(key);
  }
}
