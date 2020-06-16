package org.folio.reader.values;

import org.folio.processor.rule.DataSource;

import java.util.List;

public class ListValue extends SimpleValue<List<String>> {
  private List<String> value;

  public ListValue(List<String> list, DataSource dataSource) {
    this.value = list;
    this.dataSource = dataSource;
  }

  @Override
  public List<String> getValue() {
    return this.value;
  }

  public void setValue(List<String> value) {
    this.value = value;
  }

  @Override
  public SubType getSubType() {
    return SubType.LIST_OF_STRING;
  }
}
