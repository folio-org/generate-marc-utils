package org.folio.reader.values;

import java.util.ArrayList;
import java.util.List;

public class CompositeValue implements RuleValue<List<List<StringValue>>> {
  private List<List<StringValue>> value = new ArrayList<>();

  @Override
  public List<List<StringValue>> getValue() {
    return value;
  }

  @Override
  public Type getType() {
    return Type.COMPOSITE;
  }

  public boolean addEntry(List<StringValue> entry) {
    return this.value.add(entry);
  }
}
