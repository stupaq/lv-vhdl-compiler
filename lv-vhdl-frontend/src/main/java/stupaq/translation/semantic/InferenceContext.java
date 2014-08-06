package stupaq.translation.semantic;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

import java.util.Map;

import stupaq.translation.naming.IOReference;
import stupaq.vhdl93.ast.expression;

public class InferenceContext extends ForwardingMap<IOReference, expression> {
  private final Map<IOReference, expression> delegate = Maps.newHashMap();

  @Override
  protected Map<IOReference, expression> delegate() {
    return delegate;
  }
}
