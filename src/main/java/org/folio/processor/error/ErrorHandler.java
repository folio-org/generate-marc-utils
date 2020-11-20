package org.folio.processor.error;

import org.folio.processor.RuleProcessor;

/**
 * This class is intended to handle errors occur in translation process
 * @see RuleProcessor
 */
@FunctionalInterface
public interface ErrorHandler {

  /**
   *  The method is called by the RuleProcessor when Exception is thrown
   */
  void handle(TranslationException translationException);
}
