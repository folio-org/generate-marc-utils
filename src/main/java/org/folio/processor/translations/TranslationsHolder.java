package org.folio.processor.translations;

import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.folio.processor.rule.Metadata;
import org.folio.processor.ReferenceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

import static java.lang.String.format;

public enum TranslationsHolder implements TranslationFunction {

  SET_VALUE() {
    @Override
    public String apply(String value, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      return translation.getParameter("value");
    }
  },
  SET_NATURE_OF_CONTENT_TERM() {
    @Override
    public String apply(String id, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      JsonObject entry = referenceData.getByKey("natureOfContentTerms").get(id);
      if (entry == null) {
        LOGGER.error("Nature of content term is not found by the given id: {}", id);
        return StringUtils.EMPTY;
      } else {
        return entry.getString("name");
      }
    }
  },
  SET_IDENTIFIER() {
    @Override
    public String apply(String identifierValue, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      Object metadataIdentifierTypeIds = metadata.getData().get("identifierTypeId").getData();
      if (metadataIdentifierTypeIds != null) {
        List<String> identifierTypeIds = (List<String>) metadataIdentifierTypeIds;
        if (!identifierTypeIds.isEmpty()) {
          String identifierTypeId = identifierTypeIds.get(currentIndex);
          JsonObject identifierType = referenceData.getByKey("identifierTypes").get(identifierTypeId);
          if (identifierType != null && identifierType.getString("name").equals(translation.getParameter("type"))) {
            return identifierValue;
          }
        }
      }
      return StringUtils.EMPTY;
    }
  },
  SET_CONTRIBUTOR() {
    @Override
    public String apply(String identifierValue, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      Object metadataContributorNameTypeIds = metadata.getData().get("contributorNameTypeId").getData();
      if (metadataContributorNameTypeIds != null) {
        List<String> contributorNameTypeIds = (List<String>) metadataContributorNameTypeIds;
        if (!contributorNameTypeIds.isEmpty()) {
          String contributorNameTypeId = contributorNameTypeIds.get(currentIndex);
          JsonObject contributorNameType = referenceData.getByKey("contributorNameTypes").get(contributorNameTypeId);
          if (contributorNameType != null && contributorNameType.getString("name").equals(translation.getParameter("type"))) {
            return identifierValue;
          }
        }
      }
      return StringUtils.EMPTY;
    }
  },
  SET_LOCATION() {
    @Override
    public String apply(String locationId, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      JsonObject entry = referenceData.getByKey("locations").get(locationId);
      if (entry == null) {
        LOGGER.error("Location is not found by the given id: {}", locationId);
        return StringUtils.EMPTY;
      } else {
        return entry.getString("name");
      }
    }
  },
  SET_MATERIAL_TYPE() {
    @Override
    public String apply(String materialTypeId, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      JsonObject entry = referenceData.getByKey("mtypes").get(materialTypeId);
      if (entry == null) {
        LOGGER.error("Material type is not found by the given id: {}", materialTypeId);
        return StringUtils.EMPTY;
      } else {
        return entry.getString("name");
      }
    }
  },

  /**
   * Sixteen characters that indicate the date and time of the latest record transaction
   * and serve as a version identifier for the record.
   * They are recorded according to Representation of Dates and Times (ISO 8601).
   * The date requires 8 numeric characters in the pattern yyyymmdd.
   * The time requires 8 numeric characters in the pattern hhmmss.f, expressed in terms of the 24-hour (00-23) clock.
   */
  SET_TRANSACTION_DATETIME() {
    private transient DateTimeFormatter originFormatter = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
      .toFormatter();
    private transient DateTimeFormatter targetFormatter = new DateTimeFormatterBuilder()
      .appendPattern("yyyyMMddhhmmss")
      .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 1, true)
      .toFormatter();

    @Override
    public String apply(String updatedDate, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      ZonedDateTime originDateTime = ZonedDateTime.parse(updatedDate, originFormatter);
      return targetFormatter.format(originDateTime);
    }
  },

  /**
   * Forty character positions (00-39) that provide coded information about the record as a whole and about special
   * bibliographic aspects of the item being cataloged.
   * These coded data elements are potentially useful for retrieval and data management purposes.
   * Format:
   * 00-05 - Metadata.createdDate field in yymmdd format
   * 06 is set to | (pipe character)
   * 07-10 to publication[0] dateOfPublication if can be formatted else |||| (four pipe characters)
   * 11-14 to publication[1] dateOfPublication if can be formatted else |||| (four pipe characters)
   * 18-22 - each field set to |
   * 23-29 - each field to be blank
   * 30-34 - each field set to |
   * 35-37 - if languages array is empty set it to "und",
   * if one element, use it to populate the field (it should be 3 letter language code),
   * if the array contains more than one language, then set it to "mul"
   * 38-39 - each field set to |
   */
  SET_FIXED_LENGTH_DATA_ELEMENTS() {
    private transient DateTimeFormatter originCreatedDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private transient DateTimeFormatter targetCreatedDateFormatter = DateTimeFormatter.ofPattern("yyMMdd");
    private String fieldPattern = "%s|%s%s||||||||       |||||%s||";

    @Override
    public String apply(String originCreatedDate, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      String createdDateParam = targetCreatedDateFormatter.format(ZonedDateTime.parse(originCreatedDate, originCreatedDateFormatter));

      String publicationDate0Param = "||||";
      String publicationDate1Param = "||||";
      if (metadata != null && metadata.getData().containsKey("datesOfPublication")) {
        List<String> publicationDates = (List<String>) metadata.getData().get("datesOfPublication").getData();
        if (publicationDates.size() == 1 && publicationDates.get(0).length() == 4) {
          publicationDate0Param = publicationDates.get(0);
        } else if (publicationDates.size() > 1) {
          String publicationDate0 = publicationDates.get(0);
          if (publicationDate0.length() == 4) {
            publicationDate0Param = publicationDate0;
          }
          String publicationDate1 = publicationDates.get(1);
          if (publicationDate1.length() == 4) {
            publicationDate1Param = publicationDate1;
          }
        }
      }

      String languageParam = "und";
      if (metadata != null && metadata.getData().containsKey("languages")) {
        List<String> languages = (List<String>) metadata.getData().get("languages").getData();
        if (languages.size() == 1) {
          languageParam = languages.get(0);
        } else if (languages.size() > 1) {
          languageParam = "mul";
        }
      }
      return format(fieldPattern, createdDateParam, publicationDate0Param, publicationDate1Param, languageParam);
    }
  };

  public static TranslationFunction lookup(String function) {
    return valueOf(function.toUpperCase());
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


}
