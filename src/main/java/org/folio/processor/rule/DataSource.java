package org.folio.processor.rule;

import org.folio.processor.translations.Translation;

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
}
