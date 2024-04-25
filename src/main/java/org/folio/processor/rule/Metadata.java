package org.folio.processor.rule;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * Additional data used for translation a field value
 */
@Getter
@ToString
public class Metadata {
  private final Map<String, Entry> data = new HashMap<>();

  public Metadata() {
  }

  public Metadata(Map<String, String> data) {
    for (Map.Entry<String, String> entry : data.entrySet()) {
      this.data.put(entry.getKey(), new Entry(entry.getValue()));
    }
  }

  public void addData(String key, Entry entry) {
    this.data.put(key, entry);
  }

  @Data
  public static class Entry {
    private String from;
    private Object data;

    public Entry(String from) {
      this.from = from;
    }
  }
}
