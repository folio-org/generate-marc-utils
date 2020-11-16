package org.folio.processor.exception;

import org.folio.reader.record.RecordInfo;

import java.text.ParseException;

/**
 * The exception with error code and record metadata needed to identify the exact record caused the error
 */
public class MappingException extends RuntimeException {
  private final transient RecordInfo recordInfo;
  private final ErrorCode errorCode;

  public MappingException(RecordInfo recordInfo, Exception cause) {
    super(cause);
    this.recordInfo = recordInfo;
    if (cause instanceof ParseException) {
      this.errorCode = ErrorCode.DATE_PARSE_ERROR_CODE;
    } else {
      this.errorCode = ErrorCode.UNDEFINED_ERROR_CODE;
    }
  }

  public RecordInfo getRecordInfo() {
    return this.recordInfo;
  }

  public ErrorCode getErrorCode() {
    return this.errorCode;
  }
}
