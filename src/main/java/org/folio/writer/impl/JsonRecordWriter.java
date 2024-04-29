package org.folio.writer.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.folio.writer.RecordWriter;
import org.marc4j.MarcJsonWriter;
import org.marc4j.MarcWriter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * The implementation of {@link RecordWriter} writes content of marc record in JSON format
 */
public class JsonRecordWriter extends MarcRecordWriter {
  @Override
  public String getResult() {
    OutputStream outputStream = new ByteArrayOutputStream();
    MarcWriter writer = new MarcJsonWriter(outputStream);
    if (CollectionUtils.isNotEmpty(getFields())) {
      writer.write(record);
      writer.close();
      return outputStream.toString();
    } else {
      writer.close();
      return StringUtils.EMPTY;
    }
  }
}
