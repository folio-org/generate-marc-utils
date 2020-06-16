package org.folio.reader.values;

public class MissingValue implements RuleValue<MissingValue> {
  private static final MissingValue INSTANCE = new MissingValue();

  private MissingValue() {
  }

  public static MissingValue getInstance() {
    return INSTANCE;
  }

  @Override
  public MissingValue getValue() {
    return getInstance();
  }

  @Override
  public Type getType() {
    return Type.MISSING;
  }
}
