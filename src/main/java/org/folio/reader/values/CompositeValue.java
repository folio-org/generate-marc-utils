package org.folio.reader.values;

import lombok.ToString;
import org.folio.processor.rule.DataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of {@link RuleValue} contains a list of complex values assigned to different {@link DataSource}
 */
@ToString
public class CompositeValue implements RuleValue<List<List<StringValue>>> {
  private final List<List<StringValue>> value = new ArrayList<>();

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
