package stupaq.translation.errors;

public abstract class TranslationException extends RuntimeException {
  protected TranslationException() {
  }

  protected TranslationException(String message) {
    super(message);
  }

  protected TranslationException(String message, Throwable cause) {
    super(message, cause);
  }

  protected TranslationException(Throwable cause) {
    super(cause);
  }

  protected TranslationException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
