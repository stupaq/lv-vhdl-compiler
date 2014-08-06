package stupaq.translation.semantic;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

import java.util.Map;

import stupaq.translation.naming.IOReference;
import stupaq.translation.parsing.NodeRepr;

public class InferenceContext extends ForwardingMap<IOReference, NodeRepr> {
  private final Map<IOReference, NodeRepr> delegate = Maps.newHashMap();

  @Override
  protected Map<IOReference, NodeRepr> delegate() {
    return delegate;
  }
}
