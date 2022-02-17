package org.folio.processor.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecordInfoTest {

  @Test
  void testEquals() {
    RecordInfo recordInfo = new RecordInfo("id", RecordType.INSTANCE);
    RecordInfo recordInfoRef = recordInfo;
    assertEquals(recordInfo, recordInfoRef);
    assertNotEquals(null, recordInfo);
    RecordInfo recordInfoCopy = new RecordInfo("id", RecordType.INSTANCE);
    assertEquals(recordInfo, recordInfoCopy);
    RecordInfo recordInfoNotCopy = new RecordInfo("id", RecordType.HOLDING);
    assertNotEquals(recordInfo, recordInfoNotCopy);
  }
}
