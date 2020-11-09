package org.folio.processor.exception;

import java.text.ParseException;

/**
 * The exception with error code and record metadata needed to identify the exact record caused the error
 */
public class MappingException extends RuntimeException {
  private String recordId;
  private ErrorCode errorCode;

  public MappingException(String recordId, Exception cause) {
    super(cause);
    if (cause instanceof ParseException) {
      this.errorCode = ErrorCode.DATE_PARSE_ERROR_CODE;
    }
  }

  public ErrorCode getErrorCode() {
    return this.errorCode;
  }
}
