package org.folio.processor.rule;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Rule defines how to read, translate and map a field value to marc record
 */
@Getter
@ToString
public class Rule {

  @Setter
  private String field;
  @Setter
  private String indicators;
  @Setter
  private String description;
  @Setter
  private List<DataSource> dataSources;
  @Setter
  private String id;
  private Metadata metadata;
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
