package org.folio.processor.rule;

import lombok.Getter;
import lombok.Setter;
import org.folio.processor.translations.Translation;

/**
 * Data defines how to read and translate a field value, defines subfield and indicators for writing
 */

@Getter
@Setter
public class DataSource {
  private String from;
  private String subfield;
  private String indicator;
  private Translation translation;

  public DataSource copy() {
    DataSource dataSource = new DataSource();
    dataSource.setFrom(this.from);
    dataSource.setSubfield(this.subfield);
    dataSource.setIndicator(this.indicator);
    dataSource.setTranslation(this.translation);
    return dataSource;
  }
}
