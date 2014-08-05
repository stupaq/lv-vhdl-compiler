package stupaq.translation.lv2vhdl.errors;

import stupaq.labview.UID;
import stupaq.labview.VIPath;
import stupaq.translation.errors.TranslationException;

public class LocalisedException extends TranslationException {
  private UID uid;
  private VIPath vi;

  public LocalisedException(TranslationException cause) {
    super(cause);
  }

  public void setUID(UID uid) {
    this.uid = uid;
  }

  public UID getUID() {
    return uid;
  }

  public VIPath getVI() {
    return vi;
  }

  public void setVI(VIPath vi) {
    this.vi = vi;
  }

  public boolean isLocalised() {
    return uid != null && vi != null;
  }

  @Override
  public TranslationException getCause() {
    return (TranslationException) super.getCause();
  }
}
