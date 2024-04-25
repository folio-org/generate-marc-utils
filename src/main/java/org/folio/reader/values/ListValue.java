package org.folio.reader.values;

import lombok.ToString;
import org.folio.processor.error.RecordInfo;
import org.folio.processor.rule.DataSource;

import java.util.List;

/**
 * The implementation of {@link RuleValue} contains a list simple string values assigned to one {@link DataSource}
 */
@ToString
public class ListValue extends SimpleValue<List<StringValue>> {
  private List<StringValue> value;

  public ListValue(List<StringValue> list, DataSource dataSource, RecordInfo recordInfo) {
    this.value = list;
    this.dataSource = dataSource;
    this.recordInfo = recordInfo;
  }

  @Override
  public List<StringValue> getValue() {
    return this.value;
  }

  public void setValue(List<StringValue> value) {
    this.value = value;
  }

  @Override
  public SubType getSubType() {
    return SubType.LIST_OF_STRING;
  }
}
