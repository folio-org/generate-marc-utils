package org.folio.processor.rule;

import java.util.HashMap;
import java.util.Map;

public class Metadata {
  private Map<String, Entry> data = new HashMap<>();

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

  public Map<String, Entry> getData() {
    return data;
  }

  public static class Entry {
    private String from;
    private Object data;

    public Entry(String from) {
      this.from = from;
    }

    public Entry(String from, Object data) {
      this.from = from;
      this.data = data;
    }

    public String getFrom() {
      return from;
    }

    public void setFrom(String from) {
      this.from = from;
    }

    public Object getData() {
      return data;
    }

    public void setData(Object data) {
      this.data = data;
    }
  }
}
