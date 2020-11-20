package org.folio.processor.error;
import org.folio.rest.jaxrs.model.Error;

public enum ErrorCode {
  DATE_PARSE_ERROR_CODE("errorDuringParsingDate", "An error occurs during parsing the date while the mapping process"),
  UNDEFINED("undefined", "undefined");

  private final String code;
  private final String description;

  ErrorCode(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public String getCode() {
    return code;
  }

  @Override
  public String toString() {
    return code + ": " + description;
  }

  public Error toError() {
    return new Error().withCode(code).withMessage(description);
  }
}
