package org.folio.processor;

import org.folio.reader.record.RecordInfo;

/**
 * Handler to process the exceptions thrown in mapping process
 */
@FunctionalInterface
public interface ErrorHandler {

  void handle(RecordInfo recordInfo, Exception cause);
}
