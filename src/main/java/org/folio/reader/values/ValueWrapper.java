package org.folio.reader.values;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.folio.processor.error.RecordInfo;

@Getter
@ToString
@AllArgsConstructor
public class ValueWrapper {
  private RecordInfo recordInfo;
  private Object value;
}
