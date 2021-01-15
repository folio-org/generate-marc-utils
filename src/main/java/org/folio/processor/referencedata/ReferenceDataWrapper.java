package org.folio.processor.referencedata;

import java.util.Map;

/**
 * Generic interface to wrap reference data used  for translation of a specific fields
 */
public interface ReferenceDataWrapper {

  /**
   * Returns {@link Map} with reference data by key
   *
   * @param key of a specific reference data
   * @return {@link Map} with reference data {@link Map} by key
   */

  Map<String, JsonObjectWrapper> get(String key);

  /**
   * Adds reference data by key
   *
   * @param key of a specific reference data
   * @param value {@link Map} with reference data
   */
  void put(String key,  Map<String, JsonObjectWrapper> value);
}
