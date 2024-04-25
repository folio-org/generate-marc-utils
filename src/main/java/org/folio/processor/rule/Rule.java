package org.folio.processor.rule;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Rule defines how to read, translate and map a field value to marc record
 */
@Data
public class Rule {
  private String field;
  private String indicators;
  private String description;
  private List<DataSource> dataSources;
  private Metadata metadata;
  private String id;
  /* The flag means that this rule is generated from TransformationField with RecordType equals ITEM */
  private boolean isItemTypeRule;

  public void setMetadata(Map<String, String> metadata) {
    this.metadata = new Metadata(metadata);
  }

  private void setMetadataObject(Metadata metadata) {
    this.metadata = metadata;
  }

  public boolean isItemTypeRule() {
    return isItemTypeRule;
  }

  public void setItemTypeRule(boolean isItemTypeRule) {
    this.isItemTypeRule = isItemTypeRule;
  }

  public Rule copy() {
    Rule rule = new Rule();
    rule.setId(id);
    rule.setField(field);
    rule.setDescription(description);
    rule.setMetadataObject(metadata);
    List<DataSource> clonedDataSources = new ArrayList<>();
    this.dataSources.forEach(originDataSource -> clonedDataSources.add(originDataSource.copy()));
    rule.setDataSources(clonedDataSources);
    rule.setItemTypeRule(isItemTypeRule);
    return rule;
  }

}
