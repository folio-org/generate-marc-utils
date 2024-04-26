package org.folio.reader.values;


import lombok.Getter;
import lombok.Setter;
import org.folio.processor.rule.DataSource;
import org.folio.processor.error.RecordInfo;

import java.util.List;

@Getter
@Setter
public abstract class SimpleValue<T> implements RuleValue<T> {
  protected DataSource dataSource;
  protected RecordInfo recordInfo;

  public static StringValue of(String string, DataSource dataSource, RecordInfo recordInfo) {
    return new StringValue(string, dataSource, recordInfo);
  }

  public static StringValue ofNullable(DataSource dataSource) {
    return ofNullable(dataSource, null);
  }

  public static StringValue ofNullable(DataSource dataSource, RecordInfo recordInfo) {
    return new StringValue(null, dataSource, recordInfo);
  }

  public static ListValue of(List<StringValue> stringValues, DataSource dataSource) {
    return new ListValue(stringValues, dataSource, null);
  }

  @Override
  public Type getType() {
    return Type.SIMPLE;
  }

  public abstract SubType getSubType();

  public enum SubType {
    STRING,
    LIST_OF_STRING
  }
}
