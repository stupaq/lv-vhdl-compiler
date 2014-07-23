package stupaq.translation;

public abstract class Configuration extends stupaq.labview.Configuration {
  private static final String PREFIX = "translation.";
  private static final String DEPENDENCIES_FOLLOW = PREFIX + "dependencies.follow";

  public static boolean getDependenciesFollow() {
    return Boolean.valueOf(System.getProperty(DEPENDENCIES_FOLLOW, "true"));
  }
}
