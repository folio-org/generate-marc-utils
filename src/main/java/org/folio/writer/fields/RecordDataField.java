package org.folio.writer.fields;

import lombok.Getter;
import lombok.ToString;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Representation of marc data field
 */
@Getter
@ToString
public class RecordDataField {
  private final String tag;
  private char indicator1 = ' ';
  private char indicator2 = ' ';
  private final List<Map.Entry<Character, String>> subFields = new ArrayList<>();

  public RecordDataField(String tag) {
    this.tag = tag;
  }

  public void setIndicator1(Character indicator1) {
    this.indicator1 = indicator1;
  }

  public void setIndicator2(Character indicator2) {
    this.indicator2 = indicator2;
  }

  public void addSubField(Character subFieldCode, String subFieldData) {
    this.subFields.add(new SimpleEntry<>(subFieldCode, subFieldData));
  }
}
