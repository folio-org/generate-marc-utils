package org.folio.processor.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TranslationExceptionTest {

  @Test
  void testEquals() {
    TranslationException translationException = new TranslationException(new RecordInfo("id", RecordType.INSTANCE), new Exception());
    TranslationException translationExceptionRef = translationException;
    assertTrue(translationException.equals(translationExceptionRef));
    assertFalse(translationException.equals(null));
    TranslationException translationExceptionCopy = new TranslationException(new RecordInfo("id", RecordType.INSTANCE), new Exception());
    assertTrue(translationException.equals(translationExceptionCopy));
    TranslationException translationExceptionNotCopy = new TranslationException(new RecordInfo("id", RecordType.HOLDING), new Exception());
    assertFalse(translationException.equals(translationExceptionNotCopy));
  }
}
