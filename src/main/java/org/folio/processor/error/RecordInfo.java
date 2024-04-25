package org.folio.processor.error;

import lombok.Data;

/**
 * Shorten information about record that helps to identify the exact record in an instance object
 */
@Data
public class RecordInfo {
  private String id;
  private RecordType type;
  private String fieldName;
  private String fieldValue;

  public RecordInfo(String id, RecordType type) {
    this.id = id;
    this.type = type;
  }

}
