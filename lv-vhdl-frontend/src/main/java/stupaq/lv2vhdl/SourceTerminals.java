package stupaq.lv2vhdl;

import com.google.common.base.Optional;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

import java.util.Map;

import stupaq.labview.UID;
import stupaq.lv2vhdl.SourceTerminals.Source;
import stupaq.vhdl93.ast.expression;

class SourceTerminals extends ForwardingMap<UID, Source> {
  private final Map<UID, Source> delegate = Maps.newHashMap();

  @Override
  protected Map<UID, Source> delegate() {
    return delegate;
  }

  public static class Source extends Endpoint {
    public Source(String name) {
      super(name);
    }

    @Override
    public boolean isSource() {
      return true;
    }

    public Optional<expression> lvalue() {
      return value();
    }
  }
}
