package org.folio.processor.error;

import java.text.ParseException;

/**
 * This exception is thrown when an exception occurs during translation process
 */
public class TranslationException extends RuntimeException {
  private ErrorCode errorCode;
  private RecordInfo recordInfo;

  public TranslationException(RecordInfo recordInfo, Exception exception) {
    super(exception);
    this.recordInfo = recordInfo;
    if (exception instanceof ParseException) {
      this.errorCode = ErrorCode.DATE_PARSE_ERROR_CODE;
    } else {
      this.errorCode = ErrorCode.UNDEFINED;
    }
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  public RecordInfo getRecordInfo() {
    return recordInfo;
  }

  public void setRecordInfo(RecordInfo recordInfo) {
    this.recordInfo = recordInfo;
  }
}
