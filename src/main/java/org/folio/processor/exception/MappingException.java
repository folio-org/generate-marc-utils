package org.folio.processor.exception;

import java.text.ParseException;

/**
 * The exception with error code and record metadata needed to identify the exact record caused the error
 */
public class MappingException extends RuntimeException {
  private final String recordId;
  private final ErrorCode errorCode;

  public MappingException(String recordId, Exception cause) {
    super(cause);
    this.recordId = recordId;
    if (cause instanceof ParseException) {
      this.errorCode = ErrorCode.DATE_PARSE_ERROR_CODE;
    } else {
      this.errorCode = ErrorCode.UNDEFINED_ERROR_CODE;
    }
  }

  public String getRecordId() {
    return this.recordId;
  }

  public ErrorCode getErrorCode() {
    return this.errorCode;
  }
}
