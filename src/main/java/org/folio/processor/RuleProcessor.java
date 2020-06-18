package org.folio.processor;

import org.folio.processor.rule.Metadata;
import org.folio.processor.rule.Rule;
import org.folio.processor.translations.Translation;
import org.folio.processor.translations.TranslationFunction;
import org.folio.processor.translations.TranslationsHolder;
import org.folio.reader.EntityReader;
import org.folio.reader.values.CompositeValue;
import org.folio.reader.values.ListValue;
import org.folio.reader.values.RuleValue;
import org.folio.reader.values.SimpleValue;
import org.folio.reader.values.StringValue;
import org.folio.writer.RecordWriter;

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

  /**
   * Reads and translates data by given rules, writes a marc record in specific format defined by RecordWriter.
   * Returns content of the generated marc record
   *
   * @param reader
   * @param writer
   * @param referenceData
   * @param rules
   * @return content of the generated marc record
   */
  public String process(EntityReader reader, RecordWriter writer, ReferenceData referenceData, List<Rule> rules) {
    rules.forEach(rule -> {
      if (LEADER_FIELD.equals(rule.getField())) {
        rule.getDataSources().forEach(dataSource -> writer.writeLeader(dataSource.getTranslation()));
      } else {
        RuleValue ruleValue = reader.read(rule);
        switch (ruleValue.getType()) {
          case SIMPLE:
            SimpleValue simpleValue = (SimpleValue) ruleValue;
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
    });
    return writer.getResult();
  }

  private <S extends SimpleValue> void translate(S simpleValue, ReferenceData referenceData, Metadata metadata) {
    Translation translation = simpleValue.getDataSource().getTranslation();
    if (translation != null) {
      TranslationFunction translationFunction = TranslationsHolder.lookup(translation.getFunction());
      if (STRING.equals(simpleValue.getSubType())) {
        StringValue stringValue = (StringValue) simpleValue;
        String readValue = stringValue.getValue();
        String translatedValue = translationFunction.apply(readValue, 0, translation, referenceData, metadata);
        stringValue.setValue(translatedValue);
      } else if (LIST_OF_STRING.equals(simpleValue.getSubType())) {
        ListValue listValue = (ListValue) simpleValue;
        List<String> translatedValues = new ArrayList<>();
        for (int currentIndex = 0; currentIndex < listValue.getValue().size(); currentIndex++) {
          String readValue = listValue.getValue().get(currentIndex);
          String translatedValue = translationFunction.apply(readValue, currentIndex, translation, referenceData, metadata);
          translatedValues.add(translatedValue);
        }
        listValue.setValue(translatedValues);
      }
    }
  }

  private void translate(CompositeValue compositeValue, ReferenceData referenceData, Metadata metadata) {
    List<List<StringValue>> readValues = compositeValue.getValue();
    for (int currentIndex = 0; currentIndex < readValues.size(); currentIndex++) {
      List<StringValue> readEntry = readValues.get(currentIndex);
      for (StringValue stringValue : readEntry) {
        Translation translation = stringValue.getDataSource().getTranslation();
        if (translation != null) {
          TranslationFunction translationFunction = TranslationsHolder.lookup(translation.getFunction());
          String readValue = stringValue.getValue();
          String translatedValue = translationFunction.apply(readValue, currentIndex, translation, referenceData, metadata);
          stringValue.setValue(translatedValue);
        }
      }
    }
  }
}
