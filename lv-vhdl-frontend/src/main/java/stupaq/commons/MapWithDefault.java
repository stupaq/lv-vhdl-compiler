package stupaq.commons;

import com.google.common.base.Supplier;
import com.google.common.collect.ForwardingMap;

import java.util.Map;

public class MapWithDefault<K, V> extends ForwardingMap<K, V> {
  private final Map<K, V> delegate;
  private final Supplier<V> supplier;

  public MapWithDefault(Map<K, V> delegate, Supplier<V> supplier) {
    this.delegate = delegate;
    this.supplier = supplier;
  }

  @Override
  protected Map<K, V> delegate() {
    return delegate;
  }

  public V getDefault(K key) {
    V value = get(key);
    return value == null ? supplier.get() : value;
  }
}
