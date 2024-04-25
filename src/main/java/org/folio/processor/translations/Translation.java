package org.folio.processor.translations;

import lombok.Data;

import java.util.Map;

/**
 * Data contains translation function name and additional parameters for translation
 */
@Data
public class Translation {
  private String function;
  private Map<String, String> parameters;

  public String getParameter(String key) {
    return parameters.get(key);
  }
}
