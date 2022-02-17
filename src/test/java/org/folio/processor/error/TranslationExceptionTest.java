package org.folio.processor.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TranslationExceptionTest {

  @Test
  void testEquals() {
    TranslationException translationException = new TranslationException(new RecordInfo("id", RecordType.INSTANCE), new Exception());
    TranslationException translationExceptionRef = translationException;
    assertEquals(translationException, translationExceptionRef);
    assertNotEquals(translationException, null);
    TranslationException translationExceptionCopy = new TranslationException(new RecordInfo("id", RecordType.INSTANCE), new Exception());
    assertEquals(translationException, translationExceptionCopy);
    TranslationException translationExceptionNotCopy = new TranslationException(new RecordInfo("id", RecordType.HOLDING), new Exception());
    assertNotEquals(translationException, translationExceptionNotCopy);
  }
}
