package org.folio.writer.fields;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Representation of marc control field
 */
@Getter
@AllArgsConstructor
public class RecordControlField {
  private String tag;
  private String data;

  public void setData(String data) {
    this.data = data;
  }
}
