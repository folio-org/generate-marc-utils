package org.folio.processor;

import org.folio.reader.record.RecordInfo;

@FunctionalInterface
public interface ErrorHandler {

  void handle(RecordInfo recordInfo);
}
