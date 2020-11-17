package org.folio.processor;

import org.folio.reader.record.RecordInfo;

/**
 * This class is intended to handle errors occur in mapping process
 * @see RuleProcessor
 */
@FunctionalInterface
public interface ErrorHandler {

  /**
   *  The method is called by the RuleProcessor when Exception is thrown
   */
  void handle(RecordInfo recordInfo);
}
