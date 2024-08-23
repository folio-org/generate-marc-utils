package org.folio.writer.impl;

import static java.lang.Boolean.TRUE;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.folio.processor.rule.Metadata;
import org.folio.processor.translations.Translation;
import org.folio.writer.RecordWriter;
import org.folio.writer.fields.RecordControlField;
import org.folio.writer.fields.RecordDataField;
import org.marc4j.MarcStreamWriter;
import org.marc4j.MarcWriter;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;
import org.marc4j.marc.impl.SortedMarcFactoryImpl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * The implementation of {@link RecordWriter} writes content of marc record in MARC format
 */
public class MarcRecordWriter extends AbstractRecordWriter {
  private static final int NUMBER_OF_MARK_FOR_DELETION_FIELDS = 2;

  protected String encoding = StandardCharsets.UTF_8.name();
  private final MarcFactory factory = new SortedMarcFactoryImpl();
  protected Record record = factory.newRecord();

  @Override
  public void writeLeader(Translation translation, Metadata metadata) {
    if (translation.getFunction().equals("set_17-19_positions")) {
      char[] implDefined2 = new char[3];
      implDefined2[0] = translation.getParameter("position17").charAt(0);
      implDefined2[1] = translation.getParameter("position18").charAt(0);
      implDefined2[2] = translation.getParameter("position19").charAt(0);
      record.getLeader().setImplDefined2(implDefined2);
    }
    if (translation.getFunction().equals("set_status_deleted") && isMarkForDeletion(metadata)) {
      record.getLeader().setRecordStatus('d');
    }
  }

  private boolean isMarkForDeletion(Metadata metadata) {
    return metadata.getData().size() == NUMBER_OF_MARK_FOR_DELETION_FIELDS &&
      metadata.getData().containsKey("discoverySuppress") &&
      metadata.getData().containsKey("staffSuppress") &&
      metadata.getData().entrySet().stream()
        .allMatch(entry -> TRUE.equals(entry.getValue().getData()));
  }

  @Override
  public void writeControlField(RecordControlField recordControlField) {
    ControlField marcControlField = factory.newControlField(recordControlField.getTag(), recordControlField.getData());
    record.addVariableField(marcControlField);
  }

  @Override
  public void writeDataField(RecordDataField recordDataField) {
    DataField marcDataField = factory.newDataField(recordDataField.getTag(), recordDataField.getIndicator1(), recordDataField.getIndicator2());
    for (Map.Entry<Character, String> subField : recordDataField.getSubFields()) {
      Character subFieldCode = subField.getKey();
      String subFieldData = subField.getValue();
      marcDataField.addSubfield(factory.newSubfield(subFieldCode, subFieldData));
    }
    record.addVariableField(marcDataField);
  }

  @Override
  public String getResult() {
    OutputStream outputStream = new ByteArrayOutputStream();
    MarcWriter writer = new MarcStreamWriter(outputStream, encoding);
    if (CollectionUtils.isNotEmpty(getFields())) {
      writer.write(record);
      writer.close();
      return outputStream.toString();
    } else {
      writer.close();
      return StringUtils.EMPTY;
    }
  }

  @Override
  public List<VariableField> getFields() {
    return record.getVariableFields();
  }
}
