package stupaq.lv2vhdl;

import com.google.common.base.Optional;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

import java.util.Map;

import stupaq.labview.UID;
import stupaq.lv2vhdl.SinkTerminals.Sink;
import stupaq.vhdl93.ast.expression;

class SinkTerminals extends ForwardingMap<UID, Sink> {
  private final Map<UID, Sink> delegate = Maps.newHashMap();

  @Override
  protected Map<UID, Sink> delegate() {
    return delegate;
  }

  public static class Sink extends Endpoint {
    public Sink(String name) {
      super(name);
    }

    @Override
    public boolean isSource() {
      return true;
    }

    public Optional<expression> rvalue() {
      return value();
    }
  }
}
