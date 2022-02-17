package org.folio.processor.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecordInfoTest {

  @Test
  void testEquals() {
    RecordInfo recordInfo = new RecordInfo("id", RecordType.INSTANCE);
    RecordInfo recordInfoRef = recordInfo;
    assertTrue(recordInfo.equals(recordInfoRef));
    assertFalse(recordInfo.equals(null));
    RecordInfo recordInfoCopy = new RecordInfo("id", RecordType.INSTANCE);
    assertTrue(recordInfo.equals(recordInfoCopy));
    RecordInfo recordInfoNotCopy = new RecordInfo("id", RecordType.HOLDING);
    assertFalse(recordInfo.equals(recordInfoNotCopy));
  }
}
