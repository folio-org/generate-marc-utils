package org.folio.processor.translations;

import org.folio.processor.RuleProcessor;
import org.folio.processor.rule.Metadata;
import org.folio.processor.referencedata.ReferenceDataWrapper;

import java.text.ParseException;

/**
 * This interface provides a contract to call data translations.
 *
 * @see RuleProcessor
 */
@FunctionalInterface
public interface TranslationFunction {
  /**
   * Applies translation for the given value
   *
   * @param value         value of subfield or indicator
   * @param currentIndex  position of the value in parent list, applicable for composite values and list values
   * @param translation   translation
   * @param referenceData reference data from inventory-storage
   * @return translated result
   */
  String apply(String value, int currentIndex, Translation translation, ReferenceDataWrapper referenceData, Metadata metadata) throws ParseException;
}
