package org.folio.writer.impl;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.folio.writer.RecordWriter;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlWriter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * The implementation of {@link RecordWriter} writes content of marc record in XML format
 */
@Log4j2
public class XmlRecordWriter extends MarcRecordWriter {
  @Override
  public String getResult() {
    log.info("getResult:: ");
    OutputStream outputStream = new ByteArrayOutputStream();
    MarcWriter writer = new MarcXmlWriter(outputStream, encoding);
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
