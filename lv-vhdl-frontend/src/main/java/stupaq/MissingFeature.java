package stupaq;

public class MissingFeature extends RuntimeException {
  public MissingFeature(String message) {
    super(message);
  }

  public static void throwIf(boolean b, String message) {
    if (b) {
      throw new MissingFeature(message);
    }
  }
}
