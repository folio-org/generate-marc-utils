package org.folio.processor.rule;

import org.folio.processor.translations.Translation;

/**
 * Data defines how to read and translate a field value, defines subfield and indicators for writing
 */
public class DataSource {
  private String from;
  private String subfield;
  private String indicator;
  private Translation translation;

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getSubfield() {
    return subfield;
  }

  public void setSubfield(String subfield) {
    this.subfield = subfield;
  }

  public String getIndicator() {
    return indicator;
  }

  public void setIndicator(String indicator) {
    this.indicator = indicator;
  }

  public Translation getTranslation() {
    return translation;
  }

  public void setTranslation(Translation translation) {
    this.translation = translation;
  }

  public DataSource copy() {
    DataSource dataSource = new DataSource();
    dataSource.setFrom(this.from);
    dataSource.setSubfield(this.subfield);
    dataSource.setIndicator(this.indicator);
    dataSource.setTranslation(this.translation);
    return dataSource;
  }
}
