package org.folio.reader.record;

/**
 * Possible types of the marc record
 */
public enum RecordType {
  INSTANCE,
  HOLDING,
  ITEM;

  public boolean isInstance() {
    return this == INSTANCE;
  }

  public boolean isHolding() {
    return this == HOLDING;
  }

  public boolean isItem() {
    return this == ITEM;
  }
}
