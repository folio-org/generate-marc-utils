package org.folio.writer.fields;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Representation of marc data field
 */
public class RecordDataField {
  private String tag;
  private char indicator1 = ' ';
  private char indicator2 = ' ';
  private List<Map.Entry<Character, String>> subFields = new ArrayList<>();

  public RecordDataField(String tag) {
    this.tag = tag;
  }

  public String getTag() {
    return tag;
  }

  public char getIndicator1() {
    return indicator1;
  }

  public void setIndicator1(Character indicator1) {
    this.indicator1 = indicator1;
  }

  public char getIndicator2() {
    return indicator2;
  }

  public void setIndicator2(Character indicator2) {
    this.indicator2 = indicator2;
  }

  public List<Map.Entry<Character, String>> getSubFields() {
    return subFields;
  }

  public void addSubField(Character subFieldCode, String subFieldData) {
    this.subFields.add(new SimpleEntry<>(subFieldCode, subFieldData));
  }
}
