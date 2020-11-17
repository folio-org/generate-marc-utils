package org.folio.reader.record;


import java.text.ParseException;

/**
 * Shorten information about record that helps to identify the exact record in an instance object
 */
public class RecordInfo {
  private String id;
  private RecordType type;
  private Exception cause;
  private ErrorCode errorCode;

  public RecordInfo(String id, RecordType type) {
    this.id = id;
    this.type = type;
  }

  public RecordInfo addCause(Exception cause) {
    this.cause = cause;
    if (cause instanceof ParseException) {
      this.errorCode = ErrorCode.DATE_PARSE_ERROR_CODE;
    } else {
      this.errorCode = ErrorCode.UNDEFINED;
    }
    return this;
  }

  public String getId() {
    return id;
  }

  public RecordType getType() {
    return type;
  }

  public Exception getCause() {
    return cause;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
