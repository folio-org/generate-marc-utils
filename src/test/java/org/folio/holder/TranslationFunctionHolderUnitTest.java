package org.folio.holder;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.folio.processor.referencedata.ReferenceDataWrapper;
import org.folio.processor.rule.Metadata;
import org.folio.processor.translations.Translation;
import org.folio.processor.translations.TranslationFunction;
import org.folio.processor.translations.TranslationsFunctionHolder;
import org.folio.util.ReferenceDataResponseUtil;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.collect.ImmutableMap;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.folio.processor.referencedata.ReferenceDataConstants.CALL_NUMBER_TYPES;
import static org.folio.processor.referencedata.ReferenceDataConstants.CAMPUSES;
import static org.folio.processor.referencedata.ReferenceDataConstants.CONTRIBUTOR_NAME_TYPES;
import static org.folio.processor.referencedata.ReferenceDataConstants.ELECTRONIC_ACCESS_RELATIONSHIPS;
import static org.folio.processor.referencedata.ReferenceDataConstants.IDENTIFIER_TYPES;
import static org.folio.processor.referencedata.ReferenceDataConstants.INSTANCE_FORMATS;
import static org.folio.processor.referencedata.ReferenceDataConstants.INSTANCE_TYPES;
import static org.folio.processor.referencedata.ReferenceDataConstants.INSTITUTIONS;
import static org.folio.processor.referencedata.ReferenceDataConstants.LIBRARIES;
import static org.folio.processor.referencedata.ReferenceDataConstants.LOAN_TYPES;
import static org.folio.processor.referencedata.ReferenceDataConstants.LOCATIONS;
import static org.folio.processor.referencedata.ReferenceDataConstants.MATERIAL_TYPES;
import static org.folio.processor.referencedata.ReferenceDataConstants.MODE_OF_ISSUANCES;
import static org.folio.processor.referencedata.ReferenceDataConstants.NATURE_OF_CONTENT_TERMS;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class TranslationFunctionHolderUnitTest {

  private static ReferenceDataWrapper referenceData = Mockito.mock(ReferenceDataWrapper.class);

  @BeforeAll
  static void setUp() {
    Mockito.when(referenceData.get(eq(NATURE_OF_CONTENT_TERMS))).thenReturn(ReferenceDataResponseUtil.getNatureOfContentTerms());
    Mockito.when(referenceData.get(eq(IDENTIFIER_TYPES))).thenReturn(ReferenceDataResponseUtil.getIdentifierTypes());
    Mockito.when(referenceData.get(eq(CONTRIBUTOR_NAME_TYPES))).thenReturn(ReferenceDataResponseUtil.getContributorNameTypes());
    Mockito.when(referenceData.get(eq(LOCATIONS))).thenReturn(ReferenceDataResponseUtil.getLocations());
    Mockito.when(referenceData.get(eq(LOAN_TYPES))).thenReturn(ReferenceDataResponseUtil.getLoanTypes());
    Mockito.when(referenceData.get(eq(LIBRARIES))).thenReturn(ReferenceDataResponseUtil.getLibraries());
    Mockito.when(referenceData.get(eq(CAMPUSES))).thenReturn(ReferenceDataResponseUtil.getCampuses());
    Mockito.when(referenceData.get(eq(INSTITUTIONS))).thenReturn(ReferenceDataResponseUtil.getInstitutions());
    Mockito.when(referenceData.get(eq(MATERIAL_TYPES))).thenReturn(ReferenceDataResponseUtil.getMaterialTypes());
    Mockito.when(referenceData.get(eq(INSTANCE_TYPES))).thenReturn(ReferenceDataResponseUtil.getInstanceTypes());
    Mockito.when(referenceData.get(eq(INSTANCE_FORMATS))).thenReturn(ReferenceDataResponseUtil.getInstanceFormats());
    Mockito.when(referenceData.get(eq(ELECTRONIC_ACCESS_RELATIONSHIPS))).thenReturn(ReferenceDataResponseUtil.getElectronicAccessRelationships());
    Mockito.when(referenceData.get(eq(MODE_OF_ISSUANCES))).thenReturn(ReferenceDataResponseUtil.getModeOfIssuances());
    Mockito.when(referenceData.get(eq(CALL_NUMBER_TYPES))).thenReturn(ReferenceDataResponseUtil.getCallNumberTypes());
  }

  @Test
  void SetValue_shouldSetGivenValue() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_VALUE;
    String value = "field value";
    Translation translation = new Translation();
    translation.setParameters(Collections.singletonMap("value", value));
    // when
    String result = translationFunction.apply(value, 0, translation, null, null);
    // then
    Assert.assertEquals(value, result);
  }

  @Test
  void SetNatureOfContentTerm_shouldReturnTermName() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_NATURE_OF_CONTENT_TERM;
    String value = "44cd89f3-2e76-469f-a955-cc57cb9e0395";
    // when
    String result = translationFunction.apply(value, 0, null, referenceData, null);
    // then
    Assert.assertEquals("textbook", result);
  }

  @Test
  void SetNatureOfContentTerm_shouldReturnEmptyString() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_NATURE_OF_CONTENT_TERM;
    String value = "non-existing-id";
    // when
    String result = translationFunction.apply(value, 0, null, referenceData, null);
    // then
    Assert.assertEquals(EMPTY, result);
  }

  @Test
  void SetIdentifier_shouldReturnIdentifierValue() throws ParseException {
    // given
    String value = "lccn value";
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_IDENTIFIER;

    Translation translation = new Translation();
    translation.setParameters(Collections.singletonMap("type", "LCCN"));

    Metadata metadata = new Metadata();
    metadata.addData("identifierType",
      new Metadata.Entry("$.identifiers[*]",
        asList(ImmutableMap.of("value", "isbn value", "identifierTypeId", "8261054f-be78-422d-bd51-4ed9f33c3422"),
               ImmutableMap.of("value", "lccn value", "identifierTypeId", "c858e4f2-2b6b-4385-842b-60732ee14abb"))));
    // when
    String result = translationFunction.apply(value, 1, translation, referenceData, metadata);
    // then
    Assert.assertEquals(value, result);
  }

  @Test
  void SetIdentifier_shouldReturnIdentifierValue_forSystemControlNumber() throws ParseException {
    // given
    String value = "system control number value";
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_IDENTIFIER;

    Translation translation = new Translation();
    translation.setParameters(Collections.singletonMap("type", "System control number"));

    Metadata metadata = new Metadata();
    metadata.addData("identifierType",
      new Metadata.Entry("$.identifiers[*]",
        singletonList(ImmutableMap.of("value", "system control number value", "identifierTypeId", "7e591197-f335-4afb-bc6d-a6d76ca3bace"))));
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, metadata);
    // then
    Assert.assertEquals(value, result);
  }

  @Test
  void SetIdentifier_shouldReturnEmptyString_whenMetadataIsEmpty() throws ParseException {
    // given
    String value = "value";
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_IDENTIFIER;

    Translation translation = new Translation();
    translation.setParameters(Collections.singletonMap("type", "LCCN"));

    Metadata metadata = new Metadata();
    metadata.addData("identifierType", new Metadata.Entry("$.identifiers[*]", Collections.emptyList()));
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, metadata);
    // then
    Assert.assertEquals(EMPTY, result);
  }

  @Test
  void SetRelatedIdentifier_shouldReturnEmptyValue_whenRelatedIdentifierDoesNotMatchCurrentIdentifierValue() throws ParseException {
    // given
    String value = "value";
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_RELATED_IDENTIFIER;

    Translation translation = new Translation();
    translation.setParameters(ImmutableMap.of(
      "relatedIdentifierTypes", "ISBN",
      "type", "Invalid ISBN"));

    Metadata metadata = new Metadata();
    metadata.addData("identifierType",
      new Metadata.Entry("$.identifiers[*]",
        asList(ImmutableMap.of("value", "invalid isbn value", "identifierTypeId", "47c7bf8e-d2a3-4b3f-84b8-79944031a55a"))));
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, metadata);
    // then
    Assert.assertEquals(EMPTY, result);
  }

  @Test
  void SetRelatedIdentifier_shouldReturnInvalidIsbnValue_whenRelatedIdentifierMatchesCurrentIdentifierValue() throws ParseException {
    // given
    String value = "isbn value";
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_RELATED_IDENTIFIER;

    Translation translation = new Translation();
    translation.setParameters(ImmutableMap.of(
      "relatedIdentifierTypes", "ISBN",
      "type", "Invalid ISBN"));

    Metadata metadata = new Metadata();
    metadata.addData("identifierType",
      new Metadata.Entry("$.identifiers[*]",
        asList(ImmutableMap.of("value", "isbn value", "identifierTypeId", "8261054f-be78-422d-bd51-4ed9f33c3422"),
               ImmutableMap.of("value", "invalid isbn value", "identifierTypeId", "47c7bf8e-d2a3-4b3f-84b8-79944031a55a"))));
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, metadata);
    // then
    Assert.assertEquals("invalid isbn value", result);
  }

  @Test
  void SetRelatedIdentifier_shouldReturnInvalidIsbnValue_whenSecondRelatedIdentifierMatchesCurrentIdentifierValue() throws ParseException {
    // given
    String value = "isbn value";
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_RELATED_IDENTIFIER;

    Translation translation = new Translation();
    translation.setParameters(ImmutableMap.of(
      "relatedIdentifierTypes", "ISSN,ISBN",
      "type", "Invalid ISBN"));

    Metadata metadata = new Metadata();
    metadata.addData("identifierType",
      new Metadata.Entry("$.identifiers[*]",
        asList(ImmutableMap.of("value", "isbn value", "identifierTypeId", "8261054f-be78-422d-bd51-4ed9f33c3422"),
               ImmutableMap.of("value", "invalid isbn value", "identifierTypeId", "47c7bf8e-d2a3-4b3f-84b8-79944031a55a"))));
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, metadata);
    // then
    Assert.assertEquals("invalid isbn value", result);
  }

  @Test
  void SetMaterialType_shouldReturnMaterialTypeValue() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_MATERIAL_TYPE;
    String value = "1a54b431-2e4f-452d-9cae-9cee66c9a892";
    // when
    String result = translationFunction.apply(value, 0, null, referenceData, null);
    // then
    Assert.assertEquals("book", result);
  }

  @Test
  void SetLocations_shouldReturnEmptyString() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_LOCATION;
    String value = "non-existing-id";
    // when
    String result = translationFunction.apply(value, 0, null, referenceData, null);
    // then
    Assert.assertEquals(EMPTY, result);
  }

  @Test
  void SetLocations_shouldReturnEmptyString_whenParametersEmpty() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_LOCATION;
    String value = "d9cd0bed-1b49-4b5e-a7bd-064b8d177231";
    Map<String, String> parameters = new HashMap<>();
    Translation translation = new Translation();
    translation.setParameters(parameters);
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, null);
    // then
    Assert.assertEquals(EMPTY, result);
  }

  @Test
  void SetLocations_shouldReturnLocationName() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_LOCATION;
    Map<String, String> parameters = new HashMap<>();
    parameters.put("field", "name");
    Translation translation = new Translation();
    translation.setParameters(parameters);
    String value = "d9cd0bed-1b49-4b5e-a7bd-064b8d177231";
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, null);
    // then
    Assert.assertEquals("Miller General Stacks", result);
  }

  @Test
  void SetLocations_shouldReturnLocationCode() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_LOCATION;
    Map<String, String> parameters = new HashMap<>();
    parameters.put("field", "code");
    Translation translation = new Translation();
    translation.setParameters(parameters);
    String value = "d9cd0bed-1b49-4b5e-a7bd-064b8d177231";
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, null);
    // then
    Assert.assertEquals("KU/CC/DI/M", result);
  }

  @Test
  void SetLocations_shouldReturnLocationLibraryName() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_LOCATION;
    Map<String, String> parameters = new HashMap<>();
    parameters.put("field", "name");
    parameters.put("referenceData", LIBRARIES);
    parameters.put("referenceDataIdField", "libraryId");
    Translation translation = new Translation();
    translation.setParameters(parameters);
    String value = "d9cd0bed-1b49-4b5e-a7bd-064b8d177231";
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, null);
    // then
    Assert.assertEquals("Main Library", result);
  }

  @Test
  void SetLocations_shouldReturnEmptyString_whenReferenceDataValueMissing() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_LOCATION;
    Map<String, String> parameters = new HashMap<>();
    parameters.put("field", "name");
    parameters.put("referenceData", LIBRARIES);
    parameters.put("referenceDataIdField", "libraryId");
    Translation translation = new Translation();
    translation.setParameters(parameters);
    String value = "non-existing";
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, null);
    // then
    Assert.assertEquals(EMPTY, result);
  }

  @Test
  void SetLocations_shouldReturnLocationLibraryCode() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_LOCATION;
    Map<String, String> parameters = new HashMap<>();
    parameters.put("field", "code");
    parameters.put("referenceData", LIBRARIES);
    parameters.put("referenceDataIdField", "libraryId");
    Translation translation = new Translation();
    translation.setParameters(parameters);
    String value = "d9cd0bed-1b49-4b5e-a7bd-064b8d177231";
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, null);
    // then
    Assert.assertEquals("ML", result);
  }

  @Test
  void SetLocations_shouldReturnLocationCampusName() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_LOCATION;
    Map<String, String> parameters = new HashMap<>();
    parameters.put("field", "name");
    parameters.put("referenceData", CAMPUSES);
    parameters.put("referenceDataIdField", "campusId");
    Translation translation = new Translation();
    translation.setParameters(parameters);
    String value = "d9cd0bed-1b49-4b5e-a7bd-064b8d177231";
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, null);
    // then
    Assert.assertEquals("Riverside Campus", result);
  }

  @Test
  void SetLocations_shouldReturnLocationCampusCode() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_LOCATION;
    Map<String, String> parameters = new HashMap<>();
    parameters.put("field", "name");
    parameters.put("referenceData", CAMPUSES);
    parameters.put("referenceDataIdField", "campusId");
    Translation translation = new Translation();
    translation.setParameters(parameters);
    String value = "d9cd0bed-1b49-4b5e-a7bd-064b8d177231";
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, null);
    // then
    Assert.assertEquals("Riverside Campus", result);
  }

  @Test
  void SetLocations_shouldReturnLocationInstitutionName() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_LOCATION;
    Map<String, String> parameters = new HashMap<>();
    parameters.put("field", "name");
    parameters.put("referenceData", INSTITUTIONS);
    parameters.put("referenceDataIdField", "institutionId");
    Translation translation = new Translation();
    translation.setParameters(parameters);
    String value = "d9cd0bed-1b49-4b5e-a7bd-064b8d177231";
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, null);
    // then
    Assert.assertEquals("Main Library", result);
  }

  @Test
  void SetLocations_shouldReturnLocationInstitutionCode() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_LOCATION;
    Map<String, String> parameters = new HashMap<>();
    parameters.put("field", "code");
    parameters.put("referenceData", INSTITUTIONS);
    parameters.put("referenceDataIdField", "institutionId");
    Translation translation = new Translation();
    translation.setParameters(parameters);
    String value = "d9cd0bed-1b49-4b5e-a7bd-064b8d177231";
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, null);
    // then
    Assert.assertEquals("ML", result);
  }

  @Test
  void SetMaterialType_shouldReturnEmptyString() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_MATERIAL_TYPE;
    String value = "non-existing-id";
    // when
    String result = translationFunction.apply(value, 0, null, referenceData, null);
    // then
    Assert.assertEquals(EMPTY, result);
  }

  @Test
  void SetInstanceTypeId_shouldReturnInstanceTypeIdValue() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_INSTANCE_TYPE_ID;
    String value = "6312d172-f0cf-40f6-b27d-9fa8feaf332f";
    // when
    String result = translationFunction.apply(value, 0, null, referenceData, null);
    // then
    Assert.assertEquals("text", result);
  }

  @Test
  void SetInstanceTypeId_shouldReturnEmptyString() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_INSTANCE_TYPE_ID;
    String value = "non-existing-id";
    // when
    String result = translationFunction.apply(value, 0, null, referenceData, null);
    // then
    Assert.assertEquals(EMPTY, result);
  }

  @Test
  void SetInstanceFormatId_shouldReturnInstanceFormatIdValue() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_INSTANCE_FORMAT_ID;
    Translation translation = new Translation();
    translation.setParameters(Collections.singletonMap("value", "0"));
    String value = "7fde4e21-00b5-4de4-a90a-08a84a601aeb";
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, null);
    // then
    Assert.assertEquals("audio", result);
  }

  @Test
  void SetInstanceFormatId_shouldReturnInstanceFormatIdValue_IfNoRegexFromInventory() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_INSTANCE_FORMAT_ID;
    Translation translation = new Translation();
    translation.setParameters(Collections.singletonMap("value", "0"));
    String value = "485e3e1d-9f46-42b6-8c65-6bb7bd4b37f8";
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, null);
    // then
    Assert.assertEquals("microform", result);
  }

  @Test
  void SetInstanceFormatId_shouldReturnEmptyString_IfNoRegexFromInventory() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_INSTANCE_FORMAT_ID;
    Translation translation = new Translation();
    translation.setParameters(Collections.singletonMap("value", "1"));
    String value = "485e3e1d-9f46-42b6-8c65-6bb7bd4b37f8";
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, null);
    // then
    Assert.assertEquals(EMPTY, result);
  }

  @Test
  void SetInstanceFormatId_shouldReturnEmptyString() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_INSTANCE_FORMAT_ID;
    String value = "non-existing-id";
    // when
    String result = translationFunction.apply(value, 0, null, referenceData, null);
    // then
    Assert.assertEquals(EMPTY, result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2020-05-22T01:46:42.915+0000", "2020-05-22T01:46:42.915+00:00"})
  void SetTransactionDatetime_shouldReturnFormattedDate(String updatedDate) throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_TRANSACTION_DATETIME;
    // when
    String result = translationFunction.apply(updatedDate, 0, null, null, null);
    // then
    Assert.assertNotNull(result);
    Assert.assertEquals("20200522014642.9", result);
  }

  @Test
  void SetTransactionDatetime_shouldThrowException() {
    // given
    String updatedDate = "date in wrong format";
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_TRANSACTION_DATETIME;
    // when
    assertThrows(ParseException.class, () ->
      translationFunction.apply(updatedDate, 0, null, null, null)
    );
  }

  @Test
  void SetContributor_shouldReturnContributorNameValue() throws ParseException {
    // given
    String value = "value";
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_CONTRIBUTOR;

    Translation translation = new Translation();
    translation.setParameters(Collections.singletonMap("type", "Personal name"));

    Metadata metadata = new Metadata();
    metadata.addData("contributorNameTypeId",
      new Metadata.Entry("$.contributors[?(!(@.primary) || @.primary == false)].contributorNameTypeId",
        Arrays.asList("2b94c631-fca9-4892-a730-03ee529ffe2a", "2e48e713-17f3-4c13-a9f8-23845bb210aa")));
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, metadata);
    // then
    Assert.assertEquals(value, result);
  }

  @Test
  void setContributor_shouldReturnEmptyString_whenMetadataIsEmpty() throws ParseException {
    // given
    String value = "value";
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_CONTRIBUTOR;

    Translation translation = new Translation();
    translation.setParameters(Collections.singletonMap("type", "Personal name"));

    Metadata metadata = new Metadata();
    metadata.addData("contributorNameTypeId", new Metadata.Entry("$.instance.contributors[?(@.primary && @.primary == true)].contributorNameTypeId", Collections.emptyList()));
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, metadata);
    // then
    Assert.assertEquals(EMPTY, result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2019-08-07T03:12:01.011+0000", "2019-08-07T03:12:01.011+00:00"})
  void SetFixedLengthDataElements_noDatesOfPublication_noLanguages_specified(String createdDate) throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_FIXED_LENGTH_DATA_ELEMENTS;
    Metadata metadata = new Metadata();
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|||||||||||||||||       |||||und||", result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2019-08-07T03:12:01.011+0000", "2019-08-07T03:12:01.011+00:00"})
  void SetFixedLengthDataElements_noDatesOfPublication_language_specified(String createdDate) throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_FIXED_LENGTH_DATA_ELEMENTS;
    Metadata metadata = new Metadata();
    metadata.addData("languages", new Metadata.Entry("$.languages", singletonList("lat")));
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|||||||||||||||||       |||||lat||", result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2019-08-07T03:12:01.011+0000", "2019-08-07T03:12:01.011+00:00"})
  void SetFixedLengthDataElements_noDatesOfPublication_multipleLanguages_specified(String createdDate) throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_FIXED_LENGTH_DATA_ELEMENTS;
    Metadata metadata = new Metadata();
    metadata.addData("languages", new Metadata.Entry("$.languages", asList("lat", "ita")));
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|||||||||||||||||       |||||mul||", result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2019-08-07T03:12:01.011+0000", "2019-08-07T03:12:01.011+00:00"})
  void SetFixedLengthDataElements_1dateOfPublication_noLanguages_specified(String createdDate) throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_FIXED_LENGTH_DATA_ELEMENTS;
    Metadata metadata = new Metadata();
    metadata.addData("datesOfPublication", new Metadata.Entry("$.publication[*].dateOfPublication", singletonList("2015")));
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|2015||||||||||||       |||||und||", result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2019-08-07T03:12:01.011+0000", "2019-08-07T03:12:01.011+00:00"})
  void SetFixedLengthDataElements_2datesOfPublication_noLanguages_specified(String createdDate) throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_FIXED_LENGTH_DATA_ELEMENTS;
    Metadata metadata = new Metadata();
    metadata.addData("datesOfPublication", new Metadata.Entry("$.publication[*].dateOfPublication", asList("2015", "2016")));
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|20152016||||||||       |||||und||", result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2019-08-07T03:12:01.011+0000", "2019-08-07T03:12:01.011+00:00"})
  void SetFixedLengthDataElements_2datesOfPublication_multipleLanguages_specified(String createdDate) throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_FIXED_LENGTH_DATA_ELEMENTS;
    Metadata metadata = new Metadata();
    metadata.addData("datesOfPublication", new Metadata.Entry("$.publication[*].dateOfPublication", asList("2015", "2016")));
    metadata.addData("languages", new Metadata.Entry("$.languages", asList("lat", "ita")));
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|20152016||||||||       |||||mul||", result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2019-08-07T03:12:01.011+0000", "2019-08-07T03:12:01.011+00:00"})
  void SetFixedLengthDataElements_2incorrectDatesOfPublication_noLanguages_specified(String createdDate) throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_FIXED_LENGTH_DATA_ELEMENTS;
    Metadata metadata = new Metadata();
    metadata.addData("datesOfPublication", new Metadata.Entry("$.publication[*].dateOfPublication", asList("123", "456")));
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|||||||||||||||||       |||||und||", result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2019-08-07T03:12:01.011+0000", "2019-08-07T03:12:01.011+00:00"})
  void SetFixedLengthDataElements_datesOfPublication_isNull_noLanguages_specified(String createdDate) throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_FIXED_LENGTH_DATA_ELEMENTS;
    Metadata metadata = new Metadata();
    metadata.addData("datesOfPublication", null);
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|||||||||||||||||       |||||und||", result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2019-08-07T03:12:01.011+0000", "2019-08-07T03:12:01.011+00:00"})
  void SetFixedLengthDataElements_datesOfPublication_isNull_languagesIsNull(String createdDate) throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_FIXED_LENGTH_DATA_ELEMENTS;
    Metadata metadata = new Metadata();
    metadata.addData("datesOfPublication", null);
    metadata.addData("languages", null);
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|||||||||||||||||       |||||und||", result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2019-08-07T03:12:01.011+0000", "2019-08-07T03:12:01.011+00:00"})
  void SetFixedLengthDataElements_datesOfPublication_isNull_languagesIsEmpty(String createdDate) throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_FIXED_LENGTH_DATA_ELEMENTS;
    Metadata metadata = new Metadata();
    metadata.addData("datesOfPublication", null);
    metadata.addData("languages", new Metadata.Entry("$.languages", singletonList(EMPTY)));
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|||||||||||||||||       |||||und||", result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2019-08-07T03:12:01.011+0000", "2019-08-07T03:12:01.011+00:00"})
  void SetFixedLengthDataElements_datesOfPublicationFirstParam_isNull_noLanguages_specified(String createdDate) throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_FIXED_LENGTH_DATA_ELEMENTS;
    Metadata metadata = new Metadata();
    metadata.addData("datesOfPublication", new Metadata.Entry("$.publication[*].dateOfPublication", asList(null, "2016")));
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|||||2016||||||||       |||||und||", result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2019-08-07T03:12:01.011+0000", "2019-08-07T03:12:01.011+00:00"})
  void SetFixedLengthDataElements_datesOfPublicationSecondParam_isNull_noLanguages_specified(String createdDate) throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_FIXED_LENGTH_DATA_ELEMENTS;
    Metadata metadata = new Metadata();
    metadata.addData("datesOfPublication", new Metadata.Entry("$.publication[*].dateOfPublication", asList("2016", null)));
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|2016||||||||||||       |||||und||", result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2019-08-07T03:12:01.011+0000", "2019-08-07T03:12:01.011+00:00"})
  void SetFixedLengthDataElements_metadataIsNull(String createdDate) throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_FIXED_LENGTH_DATA_ELEMENTS;
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, null);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|||||||||||||||||       |||||und||", result);
  }

  @Test
  void SetFixedLengthDataElements_metadataIsNull_createdDateIsNull() throws ParseException {
    // given
    String createdDate = null;
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_FIXED_LENGTH_DATA_ELEMENTS;
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, null);
    // then
    Assert.assertEquals(40, result.length());
  }

  @Test
  void SetFixedLengthDataElements_metadataIsNull_createdDateIsIncorrect() throws ParseException {
    // given
    String createdDate = "date in wrong format";
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_FIXED_LENGTH_DATA_ELEMENTS;
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, null);
    // then
    Assert.assertEquals(40, result.length());
  }

  @Test
  void SetElectronicAccessIndicator_shouldReturnEmptyIndicator_whenRelationshipIdsEmpty() throws ParseException {
    // given
    Metadata metadata = new Metadata();
    metadata.addData("relationshipId", new Metadata.Entry("$.instance.electronicAccess[*].relationshipId", Lists.emptyList()));
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_ELECTRONIC_ACCESS_INDICATOR;
    // when
    String result = translationFunction.apply(null, 0, null, null, metadata);
    // then
    Assert.assertEquals(StringUtils.SPACE, result);
  }

  @Test
  void SetElectronicAccessIndicator_shouldReturnEmptyIndicator_whenRelationshipIdNotExist() throws ParseException {
    // given
    Metadata metadata = new Metadata();
    metadata.addData("relationshipId", new Metadata.Entry("$.instance.electronicAccess[*].relationshipId", singletonList("non-existing-id")));
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_ELECTRONIC_ACCESS_INDICATOR;
    // when
    String result = translationFunction.apply(null, 0, null, referenceData, metadata);
    // then
    Assert.assertEquals(StringUtils.SPACE, result);
  }

  @Test
  void SetElectronicAccessIndicator_shouldReturnEmptyIndicator_whenRelationshipNotEqualTranslationParameterKey() throws ParseException {
    // given
    Metadata metadata = new Metadata();
    metadata.addData("relationshipId", new Metadata.Entry("$.instance.electronicAccess[*].relationshipId", singletonList("f50c90c9-bae0-4add-9cd0-db9092dbc9dd")));
    Map<String, String> parameters = new HashMap<>();
    parameters.put("Resource", "0");
    Translation translation = new Translation();
    translation.setParameters(parameters);
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_ELECTRONIC_ACCESS_INDICATOR;
    // when
    String result = translationFunction.apply(null, 0, translation, referenceData, metadata);
    // then
    Assert.assertEquals(StringUtils.SPACE, result);
  }

  @Test
  void SetElectronicAccessIndicator_shouldReturnParameterIndicator_whenRelationshipEqualsTranslationParameterKey() throws ParseException {
    // given
    Metadata metadata = new Metadata();
    metadata.addData("relationshipId", new Metadata.Entry("$.instance.electronicAccess[*].relationshipId", singletonList("f5d0068e-6272-458e-8a81-b85e7b9a14aa")));
    Map<String, String> parameters = new HashMap<>();
    parameters.put("Resource", "0");
    Translation translation = new Translation();
    translation.setParameters(parameters);
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_ELECTRONIC_ACCESS_INDICATOR;
    // when
    String result = translationFunction.apply(null, 0, translation, referenceData, metadata);
    // then
    Assert.assertEquals("0", result);
  }

  @Test
  void SetModeOfIssuanceId_shouldReturnModeOfIssuance_whenIdEqualsTranslationParameterKey() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_MODE_OF_ISSUANCE_ID;
    String value = "f5cc2ab6-bb92-4cab-b83f-5a3d09261a41";
    // when
    String result = translationFunction.apply(value, 0, null, referenceData, null);
    // then
    Assert.assertEquals("multipart monograph", result);
  }

  @Test
  void SetModeOfIssuanceId_shouldReturnEmptyValue_whenIdNotEqualsTranslationParameterKey() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_MODE_OF_ISSUANCE_ID;
    String value = "not-existing-id";
    // when
    String result = translationFunction.apply(value, 0, null, referenceData, null);
    // then
    Assert.assertEquals(EMPTY, result);
  }

  @Test
  void SetCallNumberType_shouldReturnCallNumberTypeId() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_CALL_NUMBER_TYPE_ID;
    String value = "054d460d-d6b9-4469-9e37-7a78a2266655";
    // when
    String result = translationFunction.apply(value, 0, null, referenceData, null);
    // then
    Assert.assertEquals("National Library of Medicine classification", result);
  }

  @Test
  void SetCallNumberType_shouldReturnEmptyString() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_CALL_NUMBER_TYPE_ID;
    String value = "not-existing-id";
    // when
    String result = translationFunction.apply(value, 0, null, referenceData, null);
    // then
    Assert.assertEquals(EMPTY, result);
  }

  @Test
  void SetLoanType_shouldReturnLoanValue() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_LOAN_TYPE;
    String value = "2e48e713-17f3-4c13-a9f8-23845bb210a4";
    // when
    String result = translationFunction.apply(value, 0, null, referenceData, null);
    // then
    Assert.assertEquals("Reading room", result);
  }

  @Test
  void SetLoanType_shouldReturnEmptyString() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_LOAN_TYPE;
    String value = "non-existing-id";
    // when
    String result = translationFunction.apply(value, 0, null, referenceData, null);
    // then
    Assert.assertEquals(EMPTY, result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2020-09-19T11:02:16.525+0000", "2020-09-19T11:02:16.525+00:00"})
  void SetDateTime_shouldReturnCorrectDate(String date) throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_METADATA_DATE_TIME;
    String expectedResult = "2020-09-19:11-02-16";
    // when
    String result = translationFunction.apply(date, 0, null, referenceData, null);
    // then
    Assert.assertEquals(expectedResult, result);
  }

  @Test
  void SetHoldingPermanentLocation_shouldReturnLocationName() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_HOLDINGS_PERMANENT_LOCATION;
    Translation translation = new Translation();
    String value = "d9cd0bed-1b49-4b5e-a7bd-064b8d177231";
    Metadata metadata = new Metadata();
    metadata.addData("temporaryLocationId", new Metadata.Entry("$.holdings[*].temporaryLocationId", Collections.emptyList()));
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, metadata);
    // then
    Assert.assertEquals("Miller General Stacks", result);
  }

  @Test
  void SetHoldingPermanentLocation_shouldReturnEmpty_whenTemporaryLocationIsPresent() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_HOLDINGS_PERMANENT_LOCATION;
    Translation translation = new Translation();
    String value = "d9cd0bed-1b49-4b5e-a7bd-064b8d177231";
    Metadata metadata = new Metadata();
    metadata.addData("temporaryLocationId", new Metadata.Entry("$.holdings[*].temporaryLocationId", singletonList("fbeec574-4111-11eb-b378-0242ac130002")));
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, metadata);
    // then
    Assert.assertEquals(EMPTY, result);
  }

  @Test
  void SetHoldingPermanentLocation_shouldReturnEmpty_whenLocationIdNotPresentInReferenceData() throws ParseException {
    // given
    TranslationFunction translationFunction = TranslationsFunctionHolder.SET_HOLDINGS_PERMANENT_LOCATION;
    Translation translation = new Translation();
    String value = "bb0bc416-4112-11eb-b378-0242ac130002";
    Metadata metadata = new Metadata();
    metadata.addData("temporaryLocationId", new Metadata.Entry("$.holdings[*].temporaryLocationId", Collections.emptyList()));
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, metadata);
    // then
    Assert.assertEquals(EMPTY, result);
  }


}
