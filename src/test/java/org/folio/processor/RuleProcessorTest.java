package org.folio.processor;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.folio.TestUtil;
import org.folio.processor.rule.Rule;
import org.folio.reader.EntityReader;
import org.folio.reader.JPathSyntaxEntityReader;
import org.folio.writer.RecordWriter;
import org.folio.writer.impl.JsonRecordWriter;
import org.folio.writer.impl.MarcRecordWriter;
import org.folio.writer.impl.XmlRecordWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.folio.TestUtil.readFileContentFromResources;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
class RuleProcessorTest {
  private static JsonObject entity;
  private static List<Rule> rules;
  private static ReferenceData referenceData = Mockito.mock(ReferenceData.class);

  @BeforeAll
  static void setup() {
    entity = new JsonObject(readFileContentFromResources("processor/given_entity.json"));
    rules = Arrays.asList(Json.decodeValue(readFileContentFromResources("processor/test_rules.json"), Rule[].class));
    when(referenceData.getByKey("natureOfContentTerms")).thenReturn(TestUtil.getNatureOfContentTerms());
    when(referenceData.getByKey("identifierTypes")).thenReturn(TestUtil.getIdentifierTypes());
    when(referenceData.getByKey("contributorNameTypes")).thenReturn(TestUtil.getContributorNameTypes());
    when(referenceData.getByKey("locations")).thenReturn(TestUtil.getLocations());
    when(referenceData.getByKey("mtypes")).thenReturn(TestUtil.getMaterialTypes());
  }

  @Test
  void shouldMapEntityTo_MarcRecord() {
    // given
    RuleProcessor ruleProcessor = new RuleProcessor();
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
    RuleProcessor ruleProcessor = new RuleProcessor();
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
    RuleProcessor ruleProcessor = new RuleProcessor();
    EntityReader reader = new JPathSyntaxEntityReader(entity);
    RecordWriter writer = new XmlRecordWriter();
    // when
    String actualXmlRecord = ruleProcessor.process(reader, writer, referenceData, rules);
    // then
    String expectedXmlRecord = readFileContentFromResources("processor/mapped_xml_record.xml");
    assertEquals(expectedXmlRecord, actualXmlRecord);
  }
}
