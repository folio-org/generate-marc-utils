package org.folio.reader.values;


import org.folio.processor.rule.DataSource;

public class StringValue extends SimpleValue<String> {
  private String value;

  public StringValue(String value, DataSource dataSource) {
    this.value = value;
    this.dataSource = dataSource;
  }

  @Override
  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public SubType getSubType() {
    return SubType.STRING;
  }
}
