package org.folio.reader.values;

import org.folio.processor.error.RecordInfo;

public class ValueWrapper {
  private RecordInfo recordInfo;
  private Object value;

  public ValueWrapper(RecordInfo recordInfo, Object value) {
    this.recordInfo = recordInfo;
    this.value = value;
  }

  public RecordInfo getRecordInfo() {
    return recordInfo;
  }

  public Object getValue() {
    return value;
  }
}
