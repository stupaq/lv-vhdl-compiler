package stupaq.commons;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

import java.util.Map;

public class IntegerMap<T> extends ForwardingMap<Integer, T> {
  private Map<Integer, T> delegate = Maps.newTreeMap();

  @Override
  protected Map<Integer, T> delegate() {
    return delegate;
  }
}
