package org.folio.processor.translations;

import io.vertx.core.json.JsonObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.folio.processor.ReferenceData;
import org.folio.processor.rule.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.folio.processor.translations.ReferenceDataConstants.CONTRIBUTOR_NAME_TYPES;
import static org.folio.processor.translations.ReferenceDataConstants.ELECTRONIC_ACCESS_RELATIONSHIPS;
import static org.folio.processor.translations.ReferenceDataConstants.IDENTIFIER_TYPES;
import static org.folio.processor.translations.ReferenceDataConstants.INSTANCE_FORMATS;
import static org.folio.processor.translations.ReferenceDataConstants.INSTANCE_TYPES;
import static org.folio.processor.translations.ReferenceDataConstants.LOCATIONS;
import static org.folio.processor.translations.ReferenceDataConstants.LOAN_TYPES;
import static org.folio.processor.translations.ReferenceDataConstants.MATERIAL_TYPES;
import static org.folio.processor.translations.ReferenceDataConstants.MODE_OF_ISSUANCES;
import static org.folio.processor.translations.ReferenceDataConstants.NATURE_OF_CONTENT_TERMS;

public enum TranslationsFunctionHolder implements TranslationFunction, TranslationHolder {

  SET_VALUE() {
    @Override
    public String apply(String value, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      return translation.getParameter(VALUE);
    }
  },
  SET_NATURE_OF_CONTENT_TERM() {
    @Override
    public String apply(String id, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      JsonObject entry = referenceData.get(NATURE_OF_CONTENT_TERMS).get(id);
      if (entry == null) {
        LOGGER.error("Nature of content term is not found by the given id: {}", id);
        return StringUtils.EMPTY;
      } else {
        return entry.getString(NAME);
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
          JsonObject identifierType = referenceData.get(IDENTIFIER_TYPES).get(identifierTypeId);
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
          JsonObject contributorNameType = referenceData.get(CONTRIBUTOR_NAME_TYPES).get(contributorNameTypeId);
          if (contributorNameType != null && contributorNameType.getString("name").equals(translation.getParameter("type"))) {
            return identifierValue;
          }
        }
      }
      return StringUtils.EMPTY;
    }
  },
<<<<<<< HEAD
=======
  SET_LOCATION() {
    @Override
    public String apply(String locationId, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      JsonObject entry = referenceData.get(LOCATIONS).get(locationId);
      if (entry == null) {
        LOGGER.error("Location is not found by the given id: {}", locationId);
        return StringUtils.EMPTY;
      } else {
        return entry.getString(NAME);
      }
    }
  },
>>>>>>> branch 'MDEXP-279' of https://github.com/folio-org/generate-marc-utils.git
  SET_LOAN_TYPE() {
    @Override
    public String apply(String id, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      JsonObject entry = referenceData.get(LOAN_TYPES).get(id);
      if (entry == null) {
        LOGGER.error("Loan Type is not found by the given id: {}", id);
        return StringUtils.EMPTY;
      } else {
        return entry.getString(NAME);
      }
    }
  },
  SET_MATERIAL_TYPE() {
    @Override
    public String apply(String materialTypeId, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      JsonObject entry = referenceData.get(MATERIAL_TYPES).get(materialTypeId);
      if (entry == null) {
        LOGGER.error("Material type is not found by the given id: {}", materialTypeId);
        return StringUtils.EMPTY;
      } else {
        return entry.getString(NAME);
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
    private static final String DATES_OF_PUBLICATION = "datesOfPublication";
    private static final String LANGUAGES = "languages";
    private static final String FIELD_PATTERN = "%s|%s%s||||||||       |||||%s||";

    @Override
    public String apply(String originCreatedDate, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      String createdDateParam;
      if (isNotEmpty(originCreatedDate)) {
        try {
          createdDateParam = targetCreatedDateFormatter.format(ZonedDateTime.parse(originCreatedDate, originCreatedDateFormatter));
        } catch (DateTimeParseException e) {
          LOGGER.error("Failed to parse createdDate field, the current time value will be used");
          createdDateParam = targetCreatedDateFormatter.format(ZonedDateTime.now());
        }
      } else {
        createdDateParam = targetCreatedDateFormatter.format(ZonedDateTime.now());
      }

      String publicationDate0Param = "||||";
      String publicationDate1Param = "||||";
      if (metadata != null && metadata.getData().containsKey(DATES_OF_PUBLICATION)
        && metadata.getData().get(DATES_OF_PUBLICATION) != null) {
        List<String> publicationDates = (List<String>) metadata.getData().get(DATES_OF_PUBLICATION).getData();
        if (publicationDates.size() == 1 && isNotEmpty(publicationDates.get(0)) && publicationDates.get(0).length() == 4) {
          publicationDate0Param = publicationDates.get(0);
        } else if (publicationDates.size() > 1) {
          String publicationDate0 = publicationDates.get(0);
          if (isNotEmpty(publicationDate0) && publicationDate0.length() == 4) {
            publicationDate0Param = publicationDate0;
          }
          String publicationDate1 = publicationDates.get(1);
          if (isNotEmpty(publicationDate1) && publicationDate1.length() == 4) {
            publicationDate1Param = publicationDate1;
          }
        }
      }

      String languageParam = "und";
      if (metadata != null && metadata.getData().containsKey(LANGUAGES)
        && metadata.getData().get(LANGUAGES) != null) {
        List<String> languages = (List<String>) metadata.getData().get(LANGUAGES).getData();
        if (languages.size() == 1 && isNotEmpty(languages.get(0))) {
          languageParam = languages.get(0);
        } else if (languages.size() > 1) {
          languageParam = "mul";
        }
      }
      return format(FIELD_PATTERN, createdDateParam, publicationDate0Param, publicationDate1Param, languageParam);
    }
  },

  SET_INSTANCE_TYPE_ID() {
    @Override
    public String apply(String instanceTypeId, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      JsonObject entry = referenceData.get(INSTANCE_TYPES).get(instanceTypeId);
      if (entry == null) {
        LOGGER.error("Instance type id is not found by the given id: {}", instanceTypeId);
        return StringUtils.EMPTY;
      } else {
        return entry.getString(NAME);
      }
    }
  },

  SET_INSTANCE_FORMAT_ID() {
    private static final String REGEX = "--";

    @Override
    public String apply(String instanceFormatId, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      JsonObject entry = referenceData.get(INSTANCE_FORMATS).get(instanceFormatId);
      if (entry == null) {
        LOGGER.error("Instance format is not found by the given id: {}", instanceFormatId);
        return StringUtils.EMPTY;
      } else {
        String instanceFormatIdValue = entry.getString("name");
        String[] instanceFormatsResult = instanceFormatIdValue.split(REGEX);
        if (translation.getParameter(VALUE).equals("0") && isNotBlank(instanceFormatsResult[0])) {
          return instanceFormatsResult[0].trim();
        } else if (translation.getParameter(VALUE).equals("1") && instanceFormatsResult.length > 1 && isNotBlank(instanceFormatsResult[1])) {
          return instanceFormatsResult[1].trim();
        } else {
          return StringUtils.EMPTY;
        }
      }
    }
  },

  SET_ELECTRONIC_ACCESS_INDICATOR() {
    @Override
    public String apply(String value, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      List<String> relationshipIds = (List<String>) metadata.getData().get("relationshipId").getData();
      if (CollectionUtils.isNotEmpty(relationshipIds)) {
        String relationshipId = relationshipIds.get(currentIndex);
        JsonObject relationship = referenceData.get(ELECTRONIC_ACCESS_RELATIONSHIPS).get(relationshipId);
        if (relationship != null) {
          String relationshipName = relationship.getString("name");
          for (Map.Entry<String, String> parameter : translation.getParameters().entrySet()) {
            if (relationshipName.equals(parameter.getKey())) {
              return parameter.getValue();
            }
          }
        }
      }
      return StringUtils.SPACE;
    }
  },

  SET_MODE_OF_ISSUANCE_ID() {
    @Override
    public String apply(String modeOfIssuanceId, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      JsonObject entry = referenceData.get(MODE_OF_ISSUANCES).get(modeOfIssuanceId);
      if (entry == null) {
        LOGGER.error("Mode of issuance is not found by the given id: {}", modeOfIssuanceId);
        return StringUtils.EMPTY;
      } else {
        return entry.getString(NAME);
      }
    }
  },

  SET_LOCATION() {
    @Override
    public String apply(String locationId, int currentIndex, Translation translation, ReferenceData referenceData, Metadata metadata) {
      JsonObject entry = referenceData.get(LOCATIONS).get(locationId);
      if (entry == null) {
        LOGGER.error("Location is not found by the given id: {}", locationId);
        return StringUtils.EMPTY;
      } else {
        String relatedReferenceData = translation.getParameter("referenceData");
        String referenceDataIdField = translation.getParameter("referenceDataIdField");
        String field = translation.getParameter("field");
        if(relatedReferenceData != null && referenceDataIdField != null && field != null) {
          String referenceDataIdValue = entry.getString(referenceDataIdField);
          JsonObject relatedEntry = referenceData.get(relatedReferenceData).get(referenceDataIdValue);
          if(relatedEntry == null) {
            LOGGER.error("Data related for location is not found {} by the given id: {}", relatedReferenceData, referenceDataIdValue);
            return StringUtils.EMPTY;
          } else {
            return relatedEntry.getString(field);
          }
        } else if (field != null) {
          return entry.getString(field);
        }
        return StringUtils.EMPTY;
      }
    }
  };

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String NAME = "name";
  private static final String VALUE = "value";

  @Override
  public TranslationFunction lookup(String function) {
    return valueOf(function.toUpperCase());
  }

}
