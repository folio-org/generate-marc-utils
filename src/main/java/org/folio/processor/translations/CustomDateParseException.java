package org.folio.processor.translations;

public class CustomDateParseException extends RuntimeException {

  public CustomDateParseException(String message) {
    super(message);
  }

  public CustomDateParseException(String message, Throwable cause) {
    super(message, cause);
  }

}
