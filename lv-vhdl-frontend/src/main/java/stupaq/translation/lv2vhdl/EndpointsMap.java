package stupaq.translation.lv2vhdl;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.collect.Maps;

import java.util.Map;

import stupaq.labview.UID;

class EndpointsMap {
  private final Map<UID, Endpoint> delegate = Maps.newHashMap();

  public Endpoint get(UID uid) {
    Endpoint endpoint = delegate.get(uid);
    Verify.verifyNotNull(endpoint, "Missing endpoint for uid: %s.", uid);
    return endpoint;
  }

  public void put(UID uid, Endpoint endpoint) {
    Preconditions.checkNotNull(uid);
    Preconditions.checkNotNull(endpoint);
    delegate.put(uid, endpoint);
  }
}
