package org.folio.processor;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.folio.processor.rule.Metadata;
import org.folio.processor.rule.Rule;
import org.folio.processor.translations.Translation;
import org.folio.processor.translations.TranslationFunction;
import org.folio.processor.translations.TranslationHolder;
import org.folio.reader.EntityReader;
import org.folio.reader.JPathSyntaxEntityReader;
import org.folio.writer.RecordWriter;
import org.folio.writer.impl.JsonRecordWriter;
import org.folio.writer.impl.MarcRecordWriter;
import org.folio.writer.impl.XmlRecordWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.folio.TestUtil.readFileContentFromResources;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.junit.Assert.assertEquals;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class RuleProcessorTest {
  private static JsonObject entity;
  private static List<Rule> rules;

  @Mock(lenient = true)
  private ReferenceData referenceData;
  @Mock(lenient = true)
  private TranslationHolder translationHolder;
  @Mock(lenient = true)
  private TranslationFunction createdDateTranslationFunction;
  @Mock(lenient = true)
  private TranslationFunction natureOfContentTranslationFunction;
  @Mock(lenient = true)
  private TranslationFunction setValueTranslationFunction;

  @BeforeAll
  static void beforeAll() {
    entity = new JsonObject(readFileContentFromResources("processor/given_entity.json"));
    rules = Arrays.asList(Json.decodeValue(readFileContentFromResources("processor/test_rules.json"), Rule[].class));
  }

  @BeforeEach
  public void beforeEach() {
    doReturn(createdDateTranslationFunction).when(translationHolder).lookup("set_fixed_length_data_elements");
    doReturn(natureOfContentTranslationFunction).when(translationHolder).lookup("set_nature_of_content_term");
    doReturn(setValueTranslationFunction).when(translationHolder).lookup("set_value");
    doReturn("createdDataTranslatedValue").when(createdDateTranslationFunction).apply(eq("2019-08-07T03:12:01.011+0000"), eq(0), any(Translation.class), eq(referenceData), any(Metadata.class));
    doReturn("natureOfContentTranslatedValue").when(createdDateTranslationFunction).apply(eq("44cd89f3-2e76-469f-a955-cc57cb9e0395"), eq(0), any(Translation.class), eq(referenceData), any(Metadata.class));
    doReturn("1").when(setValueTranslationFunction).apply(eq(null), eq(0), any(Translation.class), eq(referenceData), eq(null));
  }

  @Test
  void shouldMapEntityTo_MarcRecord() {
    // given
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(entity);
    RecordWriter writer = new MarcRecordWriter();
    // when
    String actualMarcRecord = ruleProcessor.process(reader, writer, referenceData, rules);
    // then
    String expectedMarcRecord = readFileContentFromResources("processor/mapped_marc_record.mrc");
    assertEquals(expectedMarcRecord, actualMarcRecord);
  }

  @Test
  void shouldMapEntityTo_JsonRecord() {
    // given
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(entity);
    RecordWriter writer = new JsonRecordWriter();
    // when
    String actualJsonRecord = ruleProcessor.process(reader, writer, referenceData, rules);
    // then
    String expectedJsonRecord = readFileContentFromResources("processor/mapped_json_record.json");
    assertEquals(expectedJsonRecord, actualJsonRecord);
  }

  @Test
  void shouldMapEntityTo_XmlRecord() {
    // given
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(entity);
    RecordWriter writer = new XmlRecordWriter();
    // when
    String actualXmlRecord = ruleProcessor.process(reader, writer, referenceData, rules);
    // then
    String expectedXmlRecord = readFileContentFromResources("processor/mapped_xml_record.xml");
    assertEquals(expectedXmlRecord, actualXmlRecord);
  }
}
