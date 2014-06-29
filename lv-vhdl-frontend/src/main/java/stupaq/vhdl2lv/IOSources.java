package stupaq.vhdl2lv;

import com.google.common.base.Verify;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

import java.util.Map;

import stupaq.labview.UID;

public class IOSources extends ForwardingMap<IOReference, UID> {
  private final Map<IOReference, UID> sources = Maps.newHashMap();

  @Override
  protected Map<IOReference, UID> delegate() {
    return sources;
  }

  @Override
  public UID put(IOReference key, UID value) {
    Verify.verify(!containsKey(key), "Multiple sources for signal: %s", key);
    return super.put(key, value);
  }
}
