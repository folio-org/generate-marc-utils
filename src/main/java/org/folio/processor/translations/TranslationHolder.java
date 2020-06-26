package org.folio.processor.translations;

import org.folio.processor.RuleProcessor;
/**
 * The root interface for holders of translation functions
 *
 * @see RuleProcessor
 */
@FunctionalInterface
public interface TranslationHolder {

  /**
   * Returns implementation of translation function
   * @param functionName name of function to lookup
   * @return translation function
   */
  TranslationFunction lookup(String functionName);
}
