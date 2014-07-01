package stupaq.vhdl2lv;

import com.google.common.base.Verify;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

import java.util.Map;

import stupaq.concepts.IOReference;
import stupaq.labview.scripting.hierarchy.Terminal;

class IOSources extends ForwardingMap<IOReference, Terminal> {
  private final Map<IOReference, Terminal> sources = Maps.newHashMap();

  @Override
  protected Map<IOReference, Terminal> delegate() {
    return sources;
  }

  @Override
  public Terminal put(IOReference key, Terminal value) {
    Verify.verify(!containsKey(key), "Multiple sources for signal: %s", key);
    return super.put(key, value);
  }

  @Override
  public void putAll(Map<? extends IOReference, ? extends Terminal> map) {
    super.standardPutAll(map);
  }
}
