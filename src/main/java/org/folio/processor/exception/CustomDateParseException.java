package org.folio.processor.exception;

public class CustomDateParseException extends RuntimeException {

  public CustomDateParseException(String message) {
    super(message);
  }

  public CustomDateParseException(String message, Throwable cause) {
    super(message, cause);
  }

}
