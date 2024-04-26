package org.folio.processor.error;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Shorten information about record that helps to identify the exact record in an instance object
 */
@Getter
@Setter
@ToString
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
