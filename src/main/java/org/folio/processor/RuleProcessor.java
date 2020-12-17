package org.folio.processor;

import org.folio.processor.error.TranslationException;
import org.folio.processor.referencedata.ReferenceData;
import org.folio.processor.rule.Metadata;
import org.folio.processor.rule.Rule;
import org.folio.processor.translations.Translation;
import org.folio.processor.translations.TranslationFunction;
import org.folio.processor.translations.TranslationHolder;
import org.folio.processor.translations.TranslationsFunctionHolder;
import org.folio.processor.error.ErrorHandler;
import org.folio.reader.EntityReader;
import org.folio.processor.error.RecordInfo;
import org.folio.reader.values.CompositeValue;
import org.folio.reader.values.ListValue;
import org.folio.reader.values.RuleValue;
import org.folio.reader.values.SimpleValue;
import org.folio.reader.values.StringValue;
import org.folio.writer.RecordWriter;
import org.marc4j.marc.VariableField;

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
  public String process(EntityReader reader, RecordWriter writer, ReferenceData referenceData, List<Rule> rules, ErrorHandler errorHandler) {
    rules.forEach(rule -> {
      if (LEADER_FIELD.equals(rule.getField())) {
        rule.getDataSources().forEach(dataSource -> writer.writeLeader(dataSource.getTranslation()));
      } else {
        processRule(reader, writer, referenceData, rule, errorHandler);
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
  public List<VariableField> processFields(EntityReader reader, RecordWriter writer, ReferenceData referenceData, List<Rule> rules, ErrorHandler errorHandler) {
    rules.forEach(rule -> {
      if (LEADER_FIELD.equals(rule.getField())) {
        rule.getDataSources().forEach(dataSource -> writer.writeLeader(dataSource.getTranslation()));
      } else {
        processRule(reader, writer, referenceData, rule, errorHandler);
      }
    });
    return writer.getFields();
  }

  /**
   * Processes the given mapping rule using reader and writer
   */
  private void processRule(EntityReader reader, RecordWriter writer, ReferenceData referenceData, Rule rule, ErrorHandler errorHandler) {
    RuleValue<?> ruleValue = reader.read(rule);
    switch (ruleValue.getType()) {
      case SIMPLE:
        SimpleValue<?> simpleValue = (SimpleValue) ruleValue;
        translate(simpleValue, referenceData, rule.getMetadata(), errorHandler);
        writer.writeField(rule.getField(), simpleValue);
        break;
      case COMPOSITE:
        CompositeValue compositeValue = (CompositeValue) ruleValue;
        translate(compositeValue, referenceData, rule.getMetadata(), errorHandler);
        writer.writeField(rule.getField(), compositeValue);
        break;
      case MISSING:
    }
  }

  /**
   * Translates the given simple value
   */
  private <S extends SimpleValue> void translate(S simpleValue, ReferenceData referenceData, Metadata metadata, ErrorHandler errorHandler) {
    if (translationHolder != null) {
      if (STRING.equals(simpleValue.getSubType())) {
        applyTranslation((StringValue) simpleValue, referenceData, metadata, 0, errorHandler);
      } else if (LIST_OF_STRING.equals(simpleValue.getSubType())) {
        applyTranslation((ListValue) simpleValue, referenceData, metadata, errorHandler);
      }
    }
  }

  /**
   * Translates the given composite value
   */
  private void translate(CompositeValue compositeValue, ReferenceData referenceData, Metadata metadata, ErrorHandler errorHandler) {
    if (translationHolder != null) {
      List<List<StringValue>> value = compositeValue.getValue();
      for (int currentIndex = 0; currentIndex < value.size(); currentIndex++) {
        List<StringValue> readEntry = value.get(currentIndex);
        for (StringValue stringValue : readEntry) {
          applyTranslation(stringValue, referenceData, metadata, currentIndex, errorHandler);
        }
      }
    }
  }

  /**
   *  Applies translation function for ListValue
   */
  private void applyTranslation(ListValue listValue, ReferenceData referenceData, Metadata metadata, ErrorHandler errorHandler) {
    Translation translation = listValue.getDataSource().getTranslation();
    if (translation != null) {
      RecordInfo recordInfo = listValue.getRecordInfo();
      for (int currentIndex = 0; currentIndex < listValue.getValue().size(); currentIndex++) {
      StringValue stringValue = listValue.getValue().get(currentIndex);
      String readValue = stringValue.getValue();
        try {
          TranslationFunction translationFunction = translationHolder.lookup(translation.getFunction());
          String translatedValue = translationFunction.apply(readValue, currentIndex, translation, referenceData, metadata);
          stringValue.setValue(translatedValue);
        } catch (Exception e) {
          errorHandler.handle(new TranslationException(recordInfo, e));
        }
      }
    }
  }

  /**
   *  Applies translation function for StringValue
   */
  private void applyTranslation(StringValue stringValue, ReferenceData referenceData, Metadata metadata, int index, ErrorHandler errorHandler) {
    Translation translation = stringValue.getDataSource().getTranslation();
    if (translation != null) {
      String readValue = stringValue.getValue();
      RecordInfo recordInfo = stringValue.getRecordInfo();
      try {
        TranslationFunction translationFunction = translationHolder.lookup(translation.getFunction());
        String translatedValue = translationFunction.apply(readValue, index, translation, referenceData, metadata);
        stringValue.setValue(translatedValue);
      } catch (Exception e) {
        errorHandler.handle(new TranslationException(recordInfo, e));
      }
    }
  }
}
