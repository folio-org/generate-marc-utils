package org.folio.processor.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.validation.constraints.NotNull;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({"message", "type", "code", "parameters"})
public class Error {
  @JsonProperty("message")
  @JsonPropertyDescription("Error message text")
  @NotNull
  private String message;
  @JsonProperty("code")
  @JsonPropertyDescription("Error message code")
  private String code;

  @JsonProperty("message")
  public String getMessage() {
    return this.message;
  }

  @JsonProperty("message")
  public void setMessage(String var1) {
    this.message = var1;
  }

  public Error withMessage(String var1) {
    this.message = var1;
    return this;
  }

  @JsonProperty("code")
  public String getCode() {
    return this.code;
  }

  @JsonProperty("code")
  public void setCode(String var1) {
    this.code = var1;
  }

  public Error withCode(String var1) {
    this.code = var1;
    return this;
  }

}

