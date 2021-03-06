package org.folio.processor.error;

/**
 * Shorten information about record that helps to identify the exact record in an instance object
 */
public class RecordInfo {
  private String id;
  private RecordType type;
  private String fieldName;
  private String fieldValue;

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

  public void setFieldName(String fieldName) { this.fieldName = fieldName; }

  public void setFieldValue(String fieldValue) { this.fieldValue = fieldValue; }

  public String getFieldName() { return fieldName; }

  public String getFieldValue() { return fieldValue; }

}
