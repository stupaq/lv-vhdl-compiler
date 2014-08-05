package stupaq.translation.lv2vhdl.errors;

import stupaq.labview.UID;
import stupaq.labview.VIPath;

public class LocalisedException extends Exception {
  private UID uid;
  private VIPath vi;

  public LocalisedException(Exception cause) {
    super(cause);
  }

  public void setUID(UID uid) {
    this.uid = uid;
  }

  public UID getUid() {
    return uid;
  }

  public VIPath getVI() {
    return vi;
  }

  public void setVI(VIPath vi) {
    this.vi = vi;
  }
}
