package org.folio.processor;

import org.folio.processor.exception.MappingException;
import org.folio.processor.rule.Metadata;
import org.folio.processor.rule.Rule;
import org.folio.processor.translations.Translation;
import org.folio.processor.translations.TranslationFunction;
import org.folio.processor.translations.TranslationHolder;
import org.folio.processor.translations.TranslationsFunctionHolder;
import org.folio.reader.EntityReader;
import org.folio.reader.values.CompositeValue;
import org.folio.reader.values.ListValue;
import org.folio.reader.values.RuleValue;
import org.folio.reader.values.SimpleValue;
import org.folio.reader.values.StringValue;
import org.folio.writer.RecordWriter;
import org.marc4j.marc.VariableField;

import java.util.ArrayList;
import java.util.List;

import static org.folio.reader.values.SimpleValue.SubType.LIST_OF_STRING;
import static org.folio.reader.values.SimpleValue.SubType.STRING;

/**
 * RuleProcessor is a central part of mapping.
 * <p>
 * High-level algorithm:
 * # read data by the given rule
 * # translate data
 * # write data
 *
 * @see EntityReader
 * @see TranslationFunction
 * @see RecordWriter
 * @see Rule
 */
public final class RuleProcessor {
  private static final String LEADER_FIELD = "leader";

  private TranslationHolder translationHolder;

  public RuleProcessor() {
    this.translationHolder = TranslationsFunctionHolder.SET_VALUE;
  }

  public RuleProcessor(TranslationHolder translationHolder) {
    this.translationHolder = translationHolder;
  }

  /**
   * Reads and translates data by given rules, writes a marc record in specific format defined by RecordWriter.
   * Returns content of the generated marc record
   *
   * @return content of the generated marc record
   */
  public String process(EntityReader reader, RecordWriter writer, ReferenceData referenceData, List<Rule> rules) {
    rules.forEach(rule -> {
      if (LEADER_FIELD.equals(rule.getField())) {
        rule.getDataSources().forEach(dataSource -> writer.writeLeader(dataSource.getTranslation()));
      } else {
        processRule(reader, writer, referenceData, rule);
      }
    });
    return writer.getResult();
  }

  /**
   * Reads and translates data by given rules, writes a list of marc record fields in specific format defined by RecordWriter.
   * Returns the list of the generated VariableField of marc record
   *
   * @return the list of the generated VariableField of marc record
   */
  public List<VariableField> processFields(EntityReader reader, RecordWriter writer, ReferenceData referenceData, List<Rule> rules) {
    rules.forEach(rule -> {
      if (LEADER_FIELD.equals(rule.getField())) {
        rule.getDataSources().forEach(dataSource -> writer.writeLeader(dataSource.getTranslation()));
      } else {
        processRule(reader, writer, referenceData, rule);
      }
    });
    return writer.getFields();
  }

  /**
   * Processes the given mapping rule using reader and writer
   */
  private void processRule(EntityReader reader, RecordWriter writer, ReferenceData referenceData, Rule rule) {
    RuleValue<?> ruleValue = reader.read(rule);
    switch (ruleValue.getType()) {
      case SIMPLE:
        SimpleValue<?> simpleValue = (SimpleValue) ruleValue;
        translate(simpleValue, referenceData, rule.getMetadata());
        writer.writeField(rule.getField(), simpleValue);
        break;
      case COMPOSITE:
        CompositeValue compositeValue = (CompositeValue) ruleValue;
        translate(compositeValue, referenceData, rule.getMetadata());
        writer.writeField(rule.getField(), compositeValue);
        break;
      case MISSING:
    }
  }


  /**
   * Translates (modifies) the given simple value
   */
  private <S extends SimpleValue> void translate(S simpleValue, ReferenceData referenceData, Metadata metadata) {
    if (translationHolder != null) {
      Translation translation = simpleValue.getDataSource().getTranslation();
      if (translation != null) {
        String recordId = simpleValue.getRecordId();
        if (STRING.equals(simpleValue.getSubType())) {
          StringValue stringValue = (StringValue) simpleValue;
          String readValue = stringValue.getValue();
          String translatedValue = applyTranslationFunction(readValue, 0, translation, referenceData, metadata, recordId);
          stringValue.setValue(translatedValue);
        } else if (LIST_OF_STRING.equals(simpleValue.getSubType())) {
          ListValue listValue = (ListValue) simpleValue;
          List<String> translatedValues = new ArrayList<>();
          for (int currentIndex = 0; currentIndex < listValue.getValue().size(); currentIndex++) {
            String readValue = listValue.getValue().get(currentIndex);
            String translatedValue = applyTranslationFunction(readValue, currentIndex, translation, referenceData, metadata, recordId);
            translatedValues.add(translatedValue);
          }
          listValue.setValue(translatedValues);
        }
      }
    }
  }

  /**
   * Translates (modifies) the given composite value
   */
  private void translate(CompositeValue compositeValue, ReferenceData referenceData, Metadata metadata) {
    if (translationHolder != null) {
      List<List<StringValue>> readValues = compositeValue.getValue();
      for (int currentIndex = 0; currentIndex < readValues.size(); currentIndex++) {
        List<StringValue> readEntry = readValues.get(currentIndex);
        for (StringValue stringValue : readEntry) {
          Translation translation = stringValue.getDataSource().getTranslation();
          String recordId = stringValue.getRecordId();
          if (translation != null) {
            String readValue = stringValue.getValue();
            String translatedValue = applyTranslationFunction(readValue, currentIndex, translation, referenceData, metadata, recordId);
            stringValue.setValue(translatedValue);
          }
        }
      }
    }
  }

  /**
   * Calls translation function for the given value
   * Throws MappingException if RuntimeException occurred
   */
  private String applyTranslationFunction(String value, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata, String recordId) {
    try {
      TranslationFunction translationFunction = translationHolder.lookup(translation.getFunction());
      return translationFunction.apply(value, currentIndex, translation, referenceData, metadata);
    } catch (Exception e) {
      throw new MappingException(recordId, e);
    }
  }
}
