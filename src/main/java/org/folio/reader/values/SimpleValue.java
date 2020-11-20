package org.folio.reader.values;


import org.folio.processor.rule.DataSource;
import org.folio.processor.error.RecordInfo;

import java.util.List;

public abstract class SimpleValue<T> implements RuleValue<T> {
  protected DataSource dataSource;
  private RecordInfo recordInfo;

  public static StringValue of(String string, DataSource dataSource) {
    return new StringValue(string, dataSource);
  }

  public static StringValue ofNullable(DataSource dataSource) {
    return new StringValue(null, dataSource);
  }

  public static ListValue of(List<String> listOfStrings, DataSource dataSource) {
    return new ListValue(listOfStrings, dataSource);
  }

  public DataSource getDataSource() {
    return this.dataSource;
  }

  public void setRecordInfo(RecordInfo recordInfo) {
    this.recordInfo = recordInfo;
  }

  public RecordInfo getRecordInfo() {
    return this.recordInfo;
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
