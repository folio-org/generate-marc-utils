package org.folio.reader.values;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.folio.processor.error.RecordInfo;

@Getter
@AllArgsConstructor
public class ValueWrapper {
  private RecordInfo recordInfo;
  private Object value;
}
