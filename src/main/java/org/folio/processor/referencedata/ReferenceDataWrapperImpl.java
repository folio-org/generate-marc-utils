package org.folio.processor.referencedata;

import java.util.Map;

public class ReferenceDataWrapperImpl implements ReferenceDataWrapper {

  private final Map<String, Map<String, JsonObjectWrapper>> referenceDataMap;

  public ReferenceDataWrapperImpl(Map<String, Map<String, JsonObjectWrapper>> referenceDataMap) {
    this.referenceDataMap = referenceDataMap;
  }

  @Override
  public Map<String, JsonObjectWrapper> get(String key) {
    return referenceDataMap.get(key);
  }

  @Override
  public void put(String key, Map<String, JsonObjectWrapper> value) {
    referenceDataMap.put(key, value);
  }
}
