package org.folio.writer.impl;

import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlWriter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class XmlRecordWriter extends MarcRecordWriter {
  @Override
  public String getResult() {
    OutputStream outputStream = new ByteArrayOutputStream();
    MarcWriter writer = new MarcXmlWriter(outputStream, encoding);
    writer.write(record);
    writer.close();
    return outputStream.toString();
  }
}
