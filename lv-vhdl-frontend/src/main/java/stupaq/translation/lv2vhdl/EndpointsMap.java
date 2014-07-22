package stupaq.translation.lv2vhdl;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

import java.util.Map;

import stupaq.labview.UID;

class EndpointsMap extends ForwardingMap<UID, Endpoint> {
  private final Map<UID, Endpoint> delegate = Maps.newHashMap();

  @Override
  protected Map<UID, Endpoint> delegate() {
    return delegate;
  }
}
