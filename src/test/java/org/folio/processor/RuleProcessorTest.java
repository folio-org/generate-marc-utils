package org.folio.processor;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.folio.processor.referencedata.ReferenceData;
import org.folio.processor.rule.DataSource;
import org.folio.processor.rule.Metadata;
import org.folio.processor.rule.Rule;
import org.folio.processor.translations.Translation;
import org.folio.processor.translations.TranslationFunction;
import org.folio.processor.translations.TranslationHolder;
import org.folio.processor.translations.TranslationsFunctionHolder;
import org.folio.processor.error.ErrorHandler;
import org.folio.reader.EntityReader;
import org.folio.reader.JPathSyntaxEntityReader;
import org.folio.processor.error.ErrorCode;
import org.folio.processor.error.RecordType;
import org.folio.writer.RecordWriter;
import org.folio.writer.impl.JsonRecordWriter;
import org.folio.writer.impl.MarcRecordWriter;
import org.folio.writer.impl.XmlRecordWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.VariableField;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.folio.util.TestUtil.readFileContentFromResources;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class RuleProcessorTest {
  private static List<Rule> rules;
  private JsonObject entity;

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
    rules = Arrays.asList(Json.decodeValue(readFileContentFromResources("processor/test_rules.json"), Rule[].class));
  }

  @BeforeEach
  public void beforeEach() throws ParseException {
    entity = new JsonObject(readFileContentFromResources("processor/given_entity.json"));
    doReturn(createdDateTranslationFunction).when(translationHolder).lookup("set_fixed_length_data_elements");
    doReturn(natureOfContentTranslationFunction).when(translationHolder).lookup("set_nature_of_content_term");
    doReturn(setValueTranslationFunction).when(translationHolder).lookup("set_value");
    doReturn("createdDataTranslatedValue").when(createdDateTranslationFunction).apply(eq("2019-08-07T03:12:01.011+0000"), eq(0), any(Translation.class), eq(referenceData), any(Metadata.class));
    doReturn("natureOfContentTranslatedValue").when(natureOfContentTranslationFunction).apply(eq("44cd89f3-2e76-469f-a955-cc57cb9e0395"), eq(0), any(Translation.class), eq(referenceData), isNull());
    doReturn("1").when(setValueTranslationFunction).apply(eq(null), eq(0), any(Translation.class), eq(referenceData), eq(null));
  }

  @Test
  void shouldMapEntityTo_MarcRecord() {
    // given
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(entity);
    RecordWriter writer = new MarcRecordWriter();
    // when
    String actualMarcRecord = ruleProcessor.process(reader, writer, referenceData, rules, null);
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
    String actualJsonRecord = ruleProcessor.process(reader, writer, referenceData, rules, null);
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
    String actualXmlRecord = ruleProcessor.process(reader, writer, referenceData, rules, null);
    // then
    String expectedXmlRecord = readFileContentFromResources("processor/mapped_xml_record.xml");
    assertEquals(expectedXmlRecord, actualXmlRecord);
  }

  @Test
  void shouldReturnVariableFieldsList_MarcRecord() {
    // given
    entity = new JsonObject(readFileContentFromResources("processor/given_entity_with_one_field.json"));
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(entity);
    RecordWriter writer = new XmlRecordWriter();
    // when
    List<VariableField> actualVariableFields = ruleProcessor.processFields(reader, writer, referenceData, rules, null);
    // then
    ControlField actualControlField = (ControlField)actualVariableFields.get(0);
    assertEquals("001", actualControlField.getTag());
    assertEquals("4bbec474-ba4d-4404-990f-afe2fc86dd3d", actualControlField.getData());
  }

  @Test
  void shouldReturnEmpty_ForEmptyEntity_MarcRecord() {
    // given
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(new JsonObject());
    RecordWriter writer = new MarcRecordWriter();
    // when
    String actualJsonRecord = ruleProcessor.process(reader, writer, referenceData, rules, null);
    // then
    assertEquals(StringUtils.EMPTY, actualJsonRecord);
  }

  @Test
  void shouldReturnEmpty_ForEmptyEntity_JsonRecord() {
    // given
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(new JsonObject());
    RecordWriter writer = new JsonRecordWriter();
    // when
    String actualJsonRecord = ruleProcessor.process(reader, writer, referenceData, rules, null);
    // then
    assertEquals(StringUtils.EMPTY, actualJsonRecord);
  }

  @Test
  void shouldReturnEmpty_ForEmptyEntity_XmlRecord() {
    // given
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(new JsonObject());
    RecordWriter writer = new XmlRecordWriter();
    // when
    String actualJsonRecord = ruleProcessor.process(reader, writer, referenceData, rules, null);
    // then
    assertEquals(StringUtils.EMPTY, actualJsonRecord);
  }

  @ParameterizedTest
  @ValueSource(strings = {"process", "processFields"})
  void shouldThrowParseException_whenDateIsInWrongFormat(String mode) {
    // given
    Rule rule = new Rule();
    rule.setField("000");
    DataSource dataSource = new DataSource();
    Translation translation = new Translation();
    translation.setFunction("set_transaction_datetime");
    dataSource.setTranslation(translation);
    dataSource.setFrom("$.metadata.updatedDate");
    rule.setDataSources(singletonList(dataSource));
    entity = new JsonObject(readFileContentFromResources("processor/given_entity_with_wrong_data.json"));
    when(translationHolder.lookup("set_transaction_datetime")).thenReturn(TranslationsFunctionHolder.SET_TRANSACTION_DATETIME);
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(entity);
    RecordWriter writer = new JsonRecordWriter();

    // when & then
    ErrorHandler errorHandler = (translationException) -> {
      assertEquals("4bbec474-ba4d-4404-990f-afe2fc86dd3d", translationException.getRecordInfo().getId());
      assertEquals(RecordType.INSTANCE, translationException.getRecordInfo().getType());
      assertEquals(ParseException.class, translationException.getCause().getClass());
      assertEquals(ErrorCode.DATE_PARSE_ERROR_CODE, translationException.getErrorCode());
    };
    if ("process".equals(mode)) {
      ruleProcessor.process(reader, writer, referenceData, singletonList(rule), errorHandler);
    }
    if ("processFields".equals(mode)) {
      ruleProcessor.processFields(reader, writer, referenceData, singletonList(rule), errorHandler);
    }
  }

  @Test
  void shouldCallErrorHandlerForLanguages() {
    // given
    Rule rule = new Rule();
    rule.setField("000");
    DataSource dataSource = new DataSource();
    Translation translation = new Translation();
    translation.setFunction("translate_languages");
    dataSource.setTranslation(translation);
    dataSource.setFrom("$.languages");
    rule.setDataSources(singletonList(dataSource));
    entity = new JsonObject(readFileContentFromResources("processor/given_entity.json"));
    when(translationHolder.lookup("translate_languages")).thenReturn((value, currentIndex, translation1, referenceData, metadata) -> {
      throw new RuntimeException("test exception");
    });
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(entity);
    RecordWriter writer = new JsonRecordWriter();

    // when & then
    ErrorHandler errorHandler = (translationException) -> {
      assertEquals("4bbec474-ba4d-4404-990f-afe2fc86dd3d", translationException.getRecordInfo().getId());
      assertEquals(RecordType.INSTANCE, translationException.getRecordInfo().getType());
      assertEquals(RuntimeException.class, translationException.getCause().getClass());
      assertEquals(ErrorCode.UNDEFINED, translationException.getErrorCode());
    };
    ruleProcessor.process(reader, writer, referenceData, singletonList(rule), errorHandler);
  }

  @Test
  void shouldCopyRule() {
    // given
    Rule givenRule = new Rule();
    givenRule.setId("test id");
    givenRule.setDescription("test description");
    givenRule.setField("test field");
    givenRule.setMetadata(Collections.singletonMap("test key", "test value"));
    givenRule.setItemTypeRule(true);
    DataSource givenDataSource = new DataSource();
    givenDataSource.setTranslation(new Translation());
    givenDataSource.setIndicator("1");
    givenDataSource.setSubfield("a");
    givenRule.setDataSources(singletonList(givenDataSource));
    // when
    Rule copiedRule = givenRule.copy();
    // then
    assertEquals(givenRule.getId(), copiedRule.getId());
    assertEquals(givenRule.getDescription(), copiedRule.getDescription());
    assertEquals(givenRule.getField(), copiedRule.getField());
    assertEquals(givenRule.getMetadata(), copiedRule.getMetadata());
    assertEquals(givenRule.isItemTypeRule(), copiedRule.isItemTypeRule());
    DataSource copiedDataSource = copiedRule.getDataSources().get(0);
    assertEquals(givenDataSource.getFrom(), copiedDataSource.getFrom());
    assertEquals(givenDataSource.getIndicator(), copiedDataSource.getIndicator());
    assertEquals(givenDataSource.getSubfield(), copiedDataSource.getSubfield());
    assertEquals(givenDataSource.getTranslation(), copiedDataSource.getTranslation());
  }
}
