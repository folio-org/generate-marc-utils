package org.folio.processor.error;

/**
 * Shorten information about record that helps to identify the exact record in an instance object
 */
public class RecordInfo {
  private String id;
  private RecordType type;

  public RecordInfo(String id, RecordType type) {
    this.id = id;
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public RecordType getType() {
    return type;
  }
}
