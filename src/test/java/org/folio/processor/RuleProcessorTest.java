package org.folio.processor;

import static java.util.Collections.singletonList;
import static org.folio.util.TestUtil.readFileContentFromResources;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.HashMap;

import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.folio.processor.error.ErrorCode;
import org.folio.processor.error.ErrorHandler;
import org.folio.processor.error.RecordType;
import org.folio.processor.referencedata.ReferenceDataWrapper;
import org.folio.processor.rule.DataSource;
import org.folio.processor.rule.Metadata;
import org.folio.processor.rule.Rule;
import org.folio.processor.translations.Translation;
import org.folio.processor.translations.TranslationFunction;
import org.folio.processor.translations.TranslationHolder;
import org.folio.processor.translations.TranslationsFunctionHolder;
import org.folio.reader.EntityReader;
import org.folio.reader.JPathSyntaxEntityReader;
import org.folio.writer.RecordWriter;
import org.folio.writer.impl.JsonRecordWriter;
import org.folio.writer.impl.MarcRecordWriter;
import org.folio.writer.impl.XmlRecordWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.VariableField;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RuleProcessorTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static List<Rule> rules;
  private String entity;
  private String holdingsStatementsEntity;
  private String holdingsNotesEntity;

  @Mock(lenient = true)
  private ReferenceDataWrapper referenceData;
  @Mock(lenient = true)
  private TranslationHolder translationHolder;
  @Mock(lenient = true)
  private TranslationFunction createdDateTranslationFunction;
  @Mock(lenient = true)
  private TranslationFunction natureOfContentTranslationFunction;
  @Mock(lenient = true)
  private TranslationFunction setValueTranslationFunction;

  @BeforeEach
  public void beforeEach() throws ParseException, JsonProcessingException {
    rules = Arrays.asList(MAPPER.readValue(readFileContentFromResources("processor/test_rules.json"), Rule[].class));
    entity = readFileContentFromResources("processor/given_entity.json");
    holdingsStatementsEntity = readFileContentFromResources("processor/given_multiple_holdings_with_multiple_holdings_statements.json");
    holdingsNotesEntity = readFileContentFromResources("processor/given_multiple_holdings_with_multiple_holdings_notes.json");

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
  void shouldMapEntityTo_MarcRecord_Deleted() {
    // given
    var entityMarkedForDeletion = readFileContentFromResources("processor/given_entity_marked_for_deletion.json");
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(entityMarkedForDeletion);
    RecordWriter writer = new MarcRecordWriter();
    // when
    String actualMarcRecord = ruleProcessor.process(reader, writer, referenceData, rules, null);
    // then
    String expectedMarcRecord = readFileContentFromResources("processor/mapped_marc_record_deleted.mrc");
    assertEquals(expectedMarcRecord, actualMarcRecord);
  }

  @Test
  void shouldMapEntityTo_JsonRecord() {
    // given
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(entity);
    RecordWriter writer = new JsonRecordWriter();
    // when
    String actualJsonRecord = ruleProcessor.process(reader, writer, referenceData, rules, null).trim();
    // then
    String expectedJsonRecord = readFileContentFromResources("processor/mapped_json_record.json").trim();
    assertEquals(expectedJsonRecord, actualJsonRecord);
  }

  @Test
  void shouldMapEntityTo_XmlRecord() {
    // given
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(entity);
    RecordWriter writer = new XmlRecordWriter();
    // when
    String actualXmlRecord = ruleProcessor.process(reader, writer, referenceData, rules, null).trim();
    // then
    String expectedXmlRecord = readFileContentFromResources("processor/mapped_xml_record.xml").trim();
    assertEquals(expectedXmlRecord, actualXmlRecord);
  }

  @Test
  void shouldReturnVariableFieldsList_MarcRecord() {
    // given
    entity = readFileContentFromResources("processor/given_entity_with_one_field.json");
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
    EntityReader reader = new JPathSyntaxEntityReader(new JSONObject().toJSONString());
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
    EntityReader reader = new JPathSyntaxEntityReader(new JSONObject().toJSONString());
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
    EntityReader reader = new JPathSyntaxEntityReader(new JSONObject().toJSONString());
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
    dataSource.setFrom("$.instance.metadata.updatedDate");
    rule.setDataSources(singletonList(dataSource));
    entity = readFileContentFromResources("processor/given_entity_with_wrong_data.json");
    when(translationHolder.lookup("set_transaction_datetime")).thenReturn(TranslationsFunctionHolder.SET_TRANSACTION_DATETIME);
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(entity);
    RecordWriter writer = new JsonRecordWriter();

    // when & then
    ErrorHandler errorHandler = translationException -> {
      assertEquals("4bbec474-ba4d-4404-990f-afe2fc86dd3d", translationException.getRecordInfo().getId());
      assertEquals(RecordType.INSTANCE, translationException.getRecordInfo().getType());
      assertEquals(ParseException.class, translationException.getCause().getClass());
      assertEquals("metadata.updatedDate", translationException.getRecordInfo().getFieldName());
      assertEquals("2020-06-17T01:46:42 test", translationException.getRecordInfo().getFieldValue());
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
  void shouldCallErrorHandlerForTitle() {
    // given
    Rule rule = new Rule();
    rule.setField("000");
    DataSource dataSource = new DataSource();
    Translation translation = new Translation();
    translation.setFunction("set_value");
    dataSource.setTranslation(translation);
    dataSource.setFrom("$.instance.title");
    rule.setDataSources(singletonList(dataSource));
    entity = readFileContentFromResources("processor/given_entity.json");
    when(translationHolder.lookup("set_value")).thenReturn((value, currentIndex, translation1, referenceDataWrapper, metadata) -> {
      throw new RuntimeException("test exception");
    });
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(entity);
    RecordWriter writer = new JsonRecordWriter();

    // when & then
    ErrorHandler errorHandler = translationException -> {
      assertEquals("4bbec474-ba4d-4404-990f-afe2fc86dd3d", translationException.getRecordInfo().getId());
      assertEquals(RecordType.INSTANCE, translationException.getRecordInfo().getType());
      assertEquals("title", translationException.getRecordInfo().getFieldName());
      assertEquals("Test title", translationException.getRecordInfo().getFieldValue());
      assertEquals(RuntimeException.class, translationException.getCause().getClass());
      assertEquals(ErrorCode.UNDEFINED, translationException.getErrorCode());
    };
    ruleProcessor.process(reader, writer, referenceData, singletonList(rule), errorHandler);
  }

  @Test
  void shouldCallErrorHandlerForHoldingsCallNumber() {
    // given
    Rule rule = new Rule();
    rule.setField("000");
    DataSource dataSource = new DataSource();
    Translation translation = new Translation();
    translation.setFunction("set_call_number_type_id");
    dataSource.setTranslation(translation);
    dataSource.setFrom("$.holdings[*].callNumber");
    rule.setDataSources(singletonList(dataSource));
    entity = readFileContentFromResources("processor/given_entity_one_holding_one_item.json");
    when(translationHolder.lookup("set_call_number_type_id")).thenReturn((value, currentIndex, translation1, referenceDataWrapper, metadata) -> {
      throw new RuntimeException("test exception");
    });
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(entity);
    RecordWriter writer = new JsonRecordWriter();

    // when & then
    ErrorHandler errorHandler = translationException -> {
      assertEquals("holding1 Id", translationException.getRecordInfo().getId());
      assertEquals(RecordType.HOLDING, translationException.getRecordInfo().getType());
      assertEquals("callNumber", translationException.getRecordInfo().getFieldName());
      assertEquals("9985 3342", translationException.getRecordInfo().getFieldValue());
      assertEquals(RuntimeException.class, translationException.getCause().getClass());
      assertEquals(ErrorCode.UNDEFINED, translationException.getErrorCode());
    };
    ruleProcessor.process(reader, writer, referenceData, singletonList(rule), errorHandler);
  }

  @Test
  void shouldCallErrorHandlerForItemBarcode() {
    // given
    Rule rule = new Rule();
    rule.setField("000");
    DataSource dataSource = new DataSource();
    Translation translation = new Translation();
    translation.setFunction("set_value");
    dataSource.setTranslation(translation);
    dataSource.setFrom("$.holdings[*].items[*].barcode");
    rule.setDataSources(singletonList(dataSource));
    entity = readFileContentFromResources("processor/given_entity_one_holding_one_item.json");
    when(translationHolder.lookup("set_value")).thenReturn((value, currentIndex, translation1, referenceDataWrapper, metadata) -> {
      throw new RuntimeException("test exception");
    });
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(entity);
    RecordWriter writer = new JsonRecordWriter();

    // when & then
    ErrorHandler errorHandler = translationException -> {
      assertEquals("item12 Id", translationException.getRecordInfo().getId());
      assertEquals(RecordType.ITEM, translationException.getRecordInfo().getType());
      assertEquals("barcode", translationException.getRecordInfo().getFieldName());
      assertEquals("barcode12", translationException.getRecordInfo().getFieldValue());
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

  // see https://issues.folio.org/browse/GMU-7
  @Test
  void shouldNotReturnEmptyMarcRecordIfMatrixNotEmpty() {
    // given
    Rule givenRule = new Rule();
    givenRule.setField("000");
    DataSource dataSourceHoldingsStatementsWithNoValue = new DataSource();
    dataSourceHoldingsStatementsWithNoValue.setSubfield("a");
    dataSourceHoldingsStatementsWithNoValue.setFrom("$.holdings[*].holdingsStatements[*].statement");
    DataSource dataSourcePermanentLocationWithValue = new DataSource();
    dataSourcePermanentLocationWithValue.setSubfield("b");
    dataSourcePermanentLocationWithValue.setFrom("$.holdings[*].permanentLocationId");
    dataSourcePermanentLocationWithValue.setTranslation(new Translation());
    dataSourcePermanentLocationWithValue.getTranslation().setFunction("set_value");
    givenRule.setDataSources(List.of(dataSourceHoldingsStatementsWithNoValue, dataSourcePermanentLocationWithValue));
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(entity);
    RecordWriter writer = new JsonRecordWriter();

    // when
    String marcRecord = ruleProcessor.process(reader, writer, referenceData, singletonList(givenRule), exc -> {});

    // then
    assertNotEquals(StringUtils.EMPTY, marcRecord);
  }

  @Test
  void shouldMapMultipleHoldingsWithMultipleHoldingsStatementsProperly() {
    when(translationHolder.lookup("set_value"))
      .thenReturn((value, currentIndex, translation1, referenceDataWrapper, metadata) ->
        value.equals("d9cd0bed-1b49-4b5e-a7bd-064b8d177231") ? "location 1" : "location 2");
    // given
    Rule givenRule = new Rule();
    givenRule.setField("899");
    DataSource dataSourceHoldingsStatements = new DataSource();
    dataSourceHoldingsStatements.setSubfield("a");
    dataSourceHoldingsStatements.setFrom("$.holdings[*].holdingsStatements[*].statement");
    DataSource dataSourcePermanentLocation = new DataSource();
    dataSourcePermanentLocation.setSubfield("b");
    dataSourcePermanentLocation.setFrom("$.holdings[*].permanentLocationId");
    DataSource dataSourceHRID = new DataSource();
    dataSourceHRID.setSubfield("c");
    dataSourceHRID.setFrom("$.holdings[*].hrid");
    dataSourcePermanentLocation.setTranslation(new Translation());
    dataSourcePermanentLocation.getTranslation().setFunction("set_value");
    Map<String, String> params = new HashMap<>();
    params.put("field", "code");
    dataSourcePermanentLocation.getTranslation().setParameters(params);
    givenRule.setDataSources(List.of(dataSourceHoldingsStatements, dataSourcePermanentLocation, dataSourceHRID));
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(holdingsStatementsEntity);
    RecordWriter writer = new JsonRecordWriter();

    // when
    String marcRecordActual = ruleProcessor.process(reader, writer, referenceData, singletonList(givenRule), exc -> {});
    String marcRecordExpected = readFileContentFromResources("processor/mapped_json_holdings_statements_record.json");

    // then
    assertEquals(marcRecordExpected.trim(), marcRecordActual.trim());
  }

  @Test
  void shouldMapHoldingsNotesOnlyToSpecificHoldingsIfMultipleHoldings() {
    Rule givenRule = new Rule();
    givenRule.setField("900");

    DataSource dataSourceHRID = new DataSource();
    dataSourceHRID.setSubfield("a");
    dataSourceHRID.setFrom("$.holdings[*].hrid");

    DataSource dataSourceHoldingsNotes = new DataSource();
    dataSourceHoldingsNotes.setSubfield("b");
    dataSourceHoldingsNotes.setFrom("$.holdings[*].notes[?(@.holdingsNoteTypeId=='c4407cc7-d79f-4609-95bd-1cefb2e2b5c8' && (!(@.staffOnly) || @.staffOnly == false))].note");

    DataSource dataSourceHoldingsNotes2 = new DataSource();
    dataSourceHoldingsNotes2.setSubfield("c");
    dataSourceHoldingsNotes2.setFrom("$.holdings[*].notes[?(@.holdingsNoteTypeId=='c4407cc7-d79f-4609-95bd-1cefb2e2b5c9' && ((@.staffOnly) || @.staffOnly == true))].note");

    DataSource dataSourceHoldingsNotes3 = new DataSource();
    dataSourceHoldingsNotes3.setSubfield("d");
    dataSourceHoldingsNotes3.setFrom("$.holdings[*].notes[?(@.holdingsNoteTypeId=='c4407cc7-d79f-4609-95bd-1cefb2e2b5c5' && (!(@.staffOnly) || @.staffOnly == false))].note");

    DataSource dataSourceHoldingsNotes4 = new DataSource();
    dataSourceHoldingsNotes4.setSubfield("e");
    dataSourceHoldingsNotes4.setFrom("$.holdings[*].notes[?(@.holdingsNoteTypeId=='c4407cc7-d79f-4609-95bd-1cefb2e2b5c6' && ((@.staffOnly) || @.staffOnly == true))].note");

    DataSource dataSourceHoldingsNotes5 = new DataSource();
    dataSourceHoldingsNotes5.setSubfield("f");
    dataSourceHoldingsNotes5.setFrom("$.holdings[*].notes[?(@.holdingsNoteTypeId=='c4407cc7-d79f-4609-95bd-1cefb2e2b5c7' && ((@.staffOnly) || @.staffOnly == true))].note");

    givenRule.setDataSources(List.of(dataSourceHRID, dataSourceHoldingsNotes, dataSourceHoldingsNotes2, dataSourceHoldingsNotes3,
      dataSourceHoldingsNotes4, dataSourceHoldingsNotes5));
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(holdingsNotesEntity);
    RecordWriter writer = new JsonRecordWriter();

    // when
    String marcRecordActual = ruleProcessor.process(reader, writer, referenceData, singletonList(givenRule), exc -> {});
    String marcRecordExpected = readFileContentFromResources("processor/mapped_json_holdings_notes_record.json");

    // then
    assertEquals(marcRecordExpected.trim(), marcRecordActual.trim());
  }

  @Test
  void shouldNotOmitCompositeFieldWithHoldingHrId_whenOneOfItemFieldsIsNullAndGoesFirstInDataSourceList() {
    // given
    entity = readFileContentFromResources("processor/entity_with_item_empty_field.json");
    RuleProcessor ruleProcessor = new RuleProcessor(translationHolder);
    EntityReader reader = new JPathSyntaxEntityReader(entity);
    RecordWriter writer = new XmlRecordWriter();
    // when
    List<VariableField> actualVariableFields = ruleProcessor.processFields(reader, writer, referenceData, rules, null);
    // then
    DataField dataField = (DataField) actualVariableFields.get(3);
    assertEquals("020", dataField.getTag());
    assertEquals(2, dataField.getSubfields().size());
    assertEquals("$zfcd64ce1-6995-48f0-840e-89ffa2288371", dataField.getSubfields().get(0).toString());
    assertEquals("$3ho00000000001", dataField.getSubfields().get(1).toString());
  }

  @Test
  void shouldNotDuplicateErrors_whenIdentifierIsWrongWithSimpleRule() {
    // given
    RuleProcessor ruleProcessor = new RuleProcessor(TranslationsFunctionHolder.SET_RELATED_IDENTIFIER);
    EntityReader reader = new JPathSyntaxEntityReader(readFileContentFromResources("processor/given_entity_with_wrong_identifier.json"));
    RecordWriter writer = new MarcRecordWriter();

    AtomicInteger times = new AtomicInteger();

    // when & then
    ErrorHandler errorHandler = translationException -> {
      assertEquals(1, times.incrementAndGet());
    };

    ruleProcessor.process(reader, writer, referenceData, rules, errorHandler);
  }

  @Test
  void shouldNotDuplicateErrors_whenIdentifierIsWrongWithCompositeRule() throws JsonProcessingException {
    // given
    RuleProcessor ruleProcessor = new RuleProcessor(TranslationsFunctionHolder.SET_RELATED_IDENTIFIER);
    EntityReader reader = new JPathSyntaxEntityReader(readFileContentFromResources("processor/given_entity_with_wrong_identifier.json"));
    RecordWriter writer = new MarcRecordWriter();

    AtomicInteger times = new AtomicInteger();

    Translation translation = new Translation();
    translation.setParameters(Map.of(
      "relatedIdentifierTypes", "ISBN",
      "type", "Invalid ISBN"));
    translation.setFunction("set_related_identifier");

    List<Rule> compositeRules = Arrays.asList(MAPPER.readValue(readFileContentFromResources("processor/test_rules.json"),
      Rule[].class)).stream().filter(rule -> rule.getField().equals("003")).toList();

    // Enrich with translation for composite rule values.
    compositeRules.forEach(r -> r.getDataSources().forEach(ds -> ds.setTranslation(translation)));

    // when & then
    ErrorHandler errorHandler = translationException -> {
      assertEquals(1, times.incrementAndGet());
    };

    ruleProcessor.process(reader, writer, referenceData, compositeRules, errorHandler);
  }

}
