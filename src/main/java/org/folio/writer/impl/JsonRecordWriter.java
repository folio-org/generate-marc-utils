package org.folio.writer.impl;

import org.marc4j.MarcJsonWriter;
import org.marc4j.MarcWriter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class JsonRecordWriter extends MarcRecordWriter {
  @Override
  public String getResult() {
    OutputStream outputStream = new ByteArrayOutputStream();
    MarcWriter writer = new MarcJsonWriter(outputStream);
    writer.write(record);
    writer.close();
    return outputStream.toString();
  }
}
