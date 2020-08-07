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
   * @param key of a specific reference data
   * @return {@link Map} with reference data {@link Map} by key
   */

  public Map<String, JsonObject> get(String key);

  /**
   * Adds reference data by key
   *
   * @param key of a specific reference data
   * @param value {@link Map} with reference data
   */
  void put(String key,  Map<String, JsonObject> value);
}
