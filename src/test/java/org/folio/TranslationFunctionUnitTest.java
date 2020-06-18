package org.folio;

import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.folio.processor.ReferenceData;
import org.folio.processor.rule.Metadata;
import org.folio.processor.translations.Translation;
import org.folio.processor.translations.TranslationFunction;
import org.folio.processor.translations.TranslationsHolder;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class TranslationFunctionUnitTest {

  private static ReferenceData referenceData = Mockito.mock(ReferenceData.class);

  @BeforeAll
  static void setUp() {
    when(referenceData.getByKey("natureOfContentTerms")).thenReturn(TestUtil.getNatureOfContentTerms());
    when(referenceData.getByKey("identifierTypes")).thenReturn(TestUtil.getIdentifierTypes());
    when(referenceData.getByKey("contributorNameTypes")).thenReturn(TestUtil.getContributorNameTypes());
    when(referenceData.getByKey("locations")).thenReturn(TestUtil.getLocations());
    when(referenceData.getByKey("mtypes")).thenReturn(TestUtil.getMaterialTypes());
  }

  @Test
  void SetValue_shouldSetGivenValue() {
    // given
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_value");
    String value = "field value";
    Translation translation = new Translation();
    translation.setParameters(Collections.singletonMap("value", value));
    // when
    String result = translationFunction.apply(value, 0, translation, null, null);
    // then
    Assert.assertEquals(value, result);
  }

  @Test
  void SetNatureOfContentTerm_shouldReturnTermName() {
    // given
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_nature_of_content_term");
    String value = "44cd89f3-2e76-469f-a955-cc57cb9e0395";
    // when
    String result = translationFunction.apply(value, 0, null, referenceData, null);
    // then
    Assert.assertEquals("textbook", result);
  }

  @Test
  void SetNatureOfContentTerm_shouldReturnEmptyString() {
    // given
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_nature_of_content_term");
    String value = "non-existing-id";
    // when
    String result = translationFunction.apply(value, 0, null, referenceData, null);
    // then
    Assert.assertEquals(StringUtils.EMPTY, result);
  }

  @Test
  void SetIdentifier_shouldReturnIdentifierValue() {
    // given
    String value = "value";
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_identifier");

    Translation translation = new Translation();
    translation.setParameters(Collections.singletonMap("type", "LCCN"));

    Metadata metadata = new Metadata();
    metadata.addData("identifierTypeId",
      new Metadata.Entry("$.identifiers[*].identifierTypeId",
        asList("8261054f-be78-422d-bd51-4ed9f33c3422", "c858e4f2-2b6b-4385-842b-60732ee14abb")));
    // when
    String result = translationFunction.apply(value, 1, translation, referenceData, metadata);
    // then
    Assert.assertEquals(value, result);
  }

  @Test
  void SetIdentifier_shouldReturnEmptyString_whenMetadataIsEmpty() {
    // given
    String value = "value";
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_identifier");

    Translation translation = new Translation();
    translation.setParameters(Collections.singletonMap("type", "LCCN"));

    Metadata metadata = new Metadata();
    metadata.addData("identifierTypeId", new Metadata.Entry("$.identifiers[*].identifierTypeId", Collections.emptyList()));
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, metadata);
    // then
    Assert.assertEquals(StringUtils.EMPTY, result);
  }


  @Test
  void SetMaterialType_shouldReturnMaterialTypeValue() {
    // given
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_material_type");
    String value = "1a54b431-2e4f-452d-9cae-9cee66c9a892";
    // when
    String result = translationFunction.apply(value, 0, null, referenceData, null);
    // then
    Assert.assertEquals("book", result);
  }

  @Test
  void SetMaterialType_shouldReturnEmptyString() {
    // given
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_material_type");
    String value = "non-existing-id";
    // when
    String result = translationFunction.apply(value, 0, null, referenceData, null);
    // then
    Assert.assertEquals(StringUtils.EMPTY, result);
  }

  @Test
  void SetTransactionDatetime_shouldReturnFormattedDate() {
    // given
    String updatedDate = "2020-05-22T01:46:42.915+0000";
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_transaction_datetime");
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
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_transaction_datetime");
    // when
    assertThrows(DateTimeParseException.class, () ->
      translationFunction.apply(updatedDate, 0, null, null, null)
    );
  }

  @Test
  void SetContributor_shouldReturnContributorNameValue() {
    // given
    String value = "value";
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_contributor");

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
  void setContributor_shouldReturnEmptyString_whenMetadataIsEmpty() {
    // given
    String value = "value";
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_contributor");

    Translation translation = new Translation();
    translation.setParameters(Collections.singletonMap("type", "Personal name"));

    Metadata metadata = new Metadata();
    metadata.addData("contributorNameTypeId", new Metadata.Entry("$.instance.contributors[?(@.primary && @.primary == true)].contributorNameTypeId", Collections.emptyList()));
    // when
    String result = translationFunction.apply(value, 0, translation, referenceData, metadata);
    // then
    Assert.assertEquals(StringUtils.EMPTY, result);
  }


  @Test
  void SetFixedLengthDataElements_noDatesOfPublication_noLanguages_specified() {
    // given
    String createdDate = "2019-08-07T03:12:01.011+0000";
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_fixed_length_data_elements");
    Metadata metadata = new Metadata();
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|||||||||||||||||       |||||und||", result);
  }

  @Test
  void SetFixedLengthDataElements_noDatesOfPublication_language_specified() {
    // given
    String createdDate = "2019-08-07T03:12:01.011+0000";
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_fixed_length_data_elements");
    Metadata metadata = new Metadata();
    metadata.addData("languages", new Metadata.Entry("$.languages", singletonList("lat")));
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|||||||||||||||||       |||||lat||", result);
  }

  @Test
  void SetFixedLengthDataElements_noDatesOfPublication_multipleLanguages_specified() {
    // given
    String createdDate = "2019-08-07T03:12:01.011+0000";
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_fixed_length_data_elements");
    Metadata metadata = new Metadata();
    metadata.addData("languages", new Metadata.Entry("$.languages", asList("lat", "ita")));
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|||||||||||||||||       |||||mul||", result);
  }

  @Test
  void SetFixedLengthDataElements_1dateOfPublication_noLanguages_specified() {
    // given
    String createdDate = "2019-08-07T03:12:01.011+0000";
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_fixed_length_data_elements");
    Metadata metadata = new Metadata();
    metadata.addData("datesOfPublication", new Metadata.Entry("$.publication[*].dateOfPublication", singletonList("2015")));
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|2015||||||||||||       |||||und||", result);
  }

  @Test
  void SetFixedLengthDataElements_2datesOfPublication_noLanguages_specified() {
    // given
    String createdDate = "2019-08-07T03:12:01.011+0000";
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_fixed_length_data_elements");
    Metadata metadata = new Metadata();
    metadata.addData("datesOfPublication", new Metadata.Entry("$.publication[*].dateOfPublication", asList("2015", "2016")));
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|20152016||||||||       |||||und||", result);
  }

  @Test
  void SetFixedLengthDataElements_2datesOfPublication_multipleLanguages_specified() {
    // given
    String createdDate = "2019-08-07T03:12:01.011+0000";
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_fixed_length_data_elements");
    Metadata metadata = new Metadata();
    metadata.addData("datesOfPublication", new Metadata.Entry("$.publication[*].dateOfPublication", asList("2015", "2016")));
    metadata.addData("languages", new Metadata.Entry("$.languages", asList("lat", "ita")));
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|20152016||||||||       |||||mul||", result);
  }

  @Test
  void SetFixedLengthDataElements_2incorrectDatesOfPublication_noLanguages_specified() {
    // given
    String createdDate = "2019-08-07T03:12:01.011+0000";
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_fixed_length_data_elements");
    Metadata metadata = new Metadata();
    metadata.addData("datesOfPublication", new Metadata.Entry("$.publication[*].dateOfPublication", asList("123", "456")));
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, metadata);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|||||||||||||||||       |||||und||", result);
  }

  @Test
  void SetFixedLengthDataElements_metadataIsNull() {
    // given
    String createdDate = "2019-08-07T03:12:01.011+0000";
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_fixed_length_data_elements");
    // when
    String result = translationFunction.apply(createdDate, 0, null, null, null);
    // then
    Assert.assertEquals(40, result.length());
    Assert.assertEquals("190807|||||||||||||||||       |||||und||", result);
  }

  @Test
  void SetFixedLengthDataElements_shouldThrowException() {
    // given
    String createdDate = "date in wrong format";
    TranslationFunction translationFunction = TranslationsHolder.lookup("set_fixed_length_data_elements");
    // when
    assertThrows(DateTimeParseException.class, () ->
      translationFunction.apply(createdDate, 0, null, null, null)
    );
  }

}
