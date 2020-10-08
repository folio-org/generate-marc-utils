package org.folio.processor.rule;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Rule defines how to read, translate and map a field value to marc record
 */
public class Rule {
  private String field;
  private String description;
  private List<DataSource> dataSources;
  private Metadata metadata;
  private String id;

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<DataSource> getDataSources() {
    return dataSources;
  }

  public void setDataSources(List<DataSource> dataSources) {
    this.dataSources = dataSources;
  }

  public String getId() { return id; }

  public void setId(String id) { this.id = id; }

  public Metadata getMetadata() {
    return this.metadata;
  }

  public void setMetadata(Map<String, String> metadata) {
    this.metadata = new Metadata(metadata);
  }

  private void setMetadataObject(Metadata metadata) {
    this.metadata = metadata;
  }

  public Rule clone() {
    Rule rule = new Rule();
    rule.setId(id);
    rule.setField(field);
    rule.setDescription(description);
    rule.setMetadataObject(metadata);
    List<DataSource> clonedDataSources = new ArrayList<>();
    this.dataSources.forEach(originDataSource -> clonedDataSources.add(originDataSource.clone()));
    rule.setDataSources(clonedDataSources);
    return rule;
  }

}
