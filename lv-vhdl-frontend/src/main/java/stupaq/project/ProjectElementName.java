package stupaq.project;

import stupaq.labview.VIPath;

public abstract class ProjectElementName {
  public abstract String elementName();

  public static ProjectElementName parse(VIPath path) {
    // FIXME
    return null;
  }
}
