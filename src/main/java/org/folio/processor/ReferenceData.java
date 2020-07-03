package org.folio.processor;

import io.vertx.core.json.JsonObject;

import java.util.Map;

/**
 * Generic interface to wrap reference data used  for translation of a specific fields
 */
public interface ReferenceData {

  /**
   * Returns {@link Map} with reference data by key
   *
   * @param referenceDataKey key of a specific reference data
   * @return {@link Map} with reference data {@link Map} by key
   */

  Map<String, JsonObject> getByKey(String referenceDataKey);

  /**
   * Adds reference data by key
   *
   * @param referenceDataKey key of a specific reference data
   * @param referenceData {@link Map} with reference data
   */
  void addByKey(String referenceDataKey,  Map<String, JsonObject> referenceData);
}
