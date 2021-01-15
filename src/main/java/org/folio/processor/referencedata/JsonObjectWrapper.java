package org.folio.processor.referencedata;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class JsonObjectWrapper {

  private Map<String, Object> map;

  public JsonObjectWrapper(Map<String, Object> map) {
    this.map = map;
  }

  public Map<String, Object> getMap() {
    return Objects.isNull(map) ? Collections.emptyMap() : map;
  }

  public void setMap(Map<String, Object> map) {
    this.map = map;
  }
}
