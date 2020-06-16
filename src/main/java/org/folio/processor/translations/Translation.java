package org.folio.processor.translations;


import java.util.Map;

public class Translation {
  private String function;
  private Map<String, String> parameters;

  public void setFunction(String function) {
    this.function = function;
  }

  public void setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
  }

  public String getFunction() {
    return function;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public String getParameter(String key) {
    return parameters.get(key);
  }
}
