package org.folio.writer.impl;

import com.google.common.base.Strings;
import org.folio.processor.rule.DataSource;
import org.folio.reader.values.CompositeValue;
import org.folio.reader.values.ListValue;
import org.folio.reader.values.SimpleValue;
import org.folio.reader.values.StringValue;
import org.folio.writer.RecordWriter;
import org.folio.writer.fields.RecordControlField;
import org.folio.writer.fields.RecordDataField;

import java.util.Collections;
import java.util.List;

import static org.folio.reader.values.SimpleValue.SubType.LIST_OF_STRING;
import static org.folio.reader.values.SimpleValue.SubType.STRING;

public abstract class AbstractRecordWriter implements RecordWriter {
  private static final String INDICATOR_1 = "1";
  private static final String INDICATOR_2 = "2";

  @Override
  public void writeField(String field, SimpleValue simpleValue) {
    DataSource dataSource = simpleValue.getDataSource();
    if (STRING.equals(simpleValue.getSubType())) {
      StringValue stringValue = (StringValue) simpleValue;
      if (isSubFieldSource(dataSource) || isIndicatorSource(dataSource)) {
        RecordDataField recordDataField = buildDataFieldForStringValues(field, Collections.singletonList(stringValue));
        writeDataField(recordDataField);
      } else {
        RecordControlField recordControlField = new RecordControlField(field, stringValue.getValue());
        writeControlField(recordControlField);
      }
    } else if (LIST_OF_STRING.equals(simpleValue.getSubType())) {
      ListValue listValue = (ListValue) simpleValue;
      if (isSubFieldSource(dataSource) || isIndicatorSource(dataSource)) {
        RecordDataField recordDataField = buildDataFieldForListOfStrings(field, listValue);
        writeDataField(recordDataField);
      } else {
        for (StringValue stringValue : listValue.getValue()) {
          RecordControlField recordControlField = new RecordControlField(field, stringValue.getValue());
          writeControlField(recordControlField);
        }
      }
    }
  }

  @Override
  public void writeField(String field, CompositeValue compositeValue) {
    for (List<StringValue> entry : compositeValue.getValue()) {
      RecordDataField recordDataField = buildDataFieldForStringValues(field, entry);
      if (!recordDataField.getSubFields().isEmpty()) {
        writeDataField(recordDataField);
      }
    }
  }

  protected abstract void writeControlField(RecordControlField recordControlField);

  protected abstract void writeDataField(RecordDataField recordDataField);

  private RecordDataField buildDataFieldForListOfStrings(String field, ListValue listValue) {
    DataSource dataSource = listValue.getDataSource();
    RecordDataField recordDataField = new RecordDataField(field);
    for (StringValue stringValue : listValue.getValue()) {
      if (isSubFieldSource(dataSource)) {
        char subFieldCode = dataSource.getSubfield().charAt(0);
        String subFieldData = stringValue.getValue();
        if (subFieldData != null) {
          recordDataField.addSubField(subFieldCode, subFieldData);
        }
      } else if (isIndicatorSource(dataSource)) {
        char indicator = stringValue.getValue().charAt(0);
        if (INDICATOR_1.equals(dataSource.getIndicator())) {
          recordDataField.setIndicator1(indicator);
        } else if (INDICATOR_2.equals(dataSource.getIndicator())) {
          recordDataField.setIndicator2(indicator);
        }
      }
    }
    return recordDataField;
  }

  private RecordDataField buildDataFieldForStringValues(String field, List<StringValue> entry) {
    RecordDataField recordDataField = new RecordDataField(field);
    for (StringValue stringValue : entry) {
      DataSource dataSource = stringValue.getDataSource();
      if (isSubFieldSource(dataSource)) {
        char subFieldCode = dataSource.getSubfield().charAt(0);
        String subFieldData = stringValue.getValue();
        if (!Strings.isNullOrEmpty(subFieldData)) {
          recordDataField.addSubField(subFieldCode, subFieldData);
        }
      } else if (isIndicatorSource(dataSource)) {
        char indicator = stringValue.getValue().charAt(0);
        if (INDICATOR_1.equals(dataSource.getIndicator())) {
          recordDataField.setIndicator1(indicator);
        } else if (INDICATOR_2.equals(dataSource.getIndicator())) {
          recordDataField.setIndicator2(indicator);
        }
      }
    }
    return recordDataField;
  }


  private boolean isIndicatorSource(DataSource dataSource) {
    return dataSource.getIndicator() != null;
  }

  private boolean isSubFieldSource(DataSource dataSource) {
    return dataSource.getSubfield() != null;
  }
}
