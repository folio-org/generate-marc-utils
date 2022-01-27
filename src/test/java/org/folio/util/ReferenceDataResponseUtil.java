package org.folio.util;

import java.util.Map;
import java.util.stream.Collectors;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.folio.processor.referencedata.JsonObjectWrapper;

import static org.folio.processor.referencedata.ReferenceDataConstants.ALTERNATIVE_TITLE_TYPES;
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
import static org.folio.util.TestUtil.readFileContentFromResources;

public class ReferenceDataResponseUtil {

  public static Map<String, JsonObjectWrapper> getNatureOfContentTerms() {
    JSONArray natureOfContentTerms = getJsonArray(
      readFileContentFromResources("mockData/inventory/get_nature_of_content_terms_response.json"),
      NATURE_OF_CONTENT_TERMS);

    return convertArrayToMap(natureOfContentTerms);
  }

  public static Map<String, JsonObjectWrapper> getIdentifierTypes() {
    JSONArray identifierTypes = getJsonArray(
      readFileContentFromResources("mockData/inventory/get_identifier_types_response.json"),
      IDENTIFIER_TYPES);

    return convertArrayToMap(identifierTypes);
  }

  public static Map<String, JsonObjectWrapper> getContributorNameTypes() {
    JSONArray contributorNameTypes = getJsonArray(
      readFileContentFromResources("mockData/inventory/get_contributor_name_types_response.json"),
      CONTRIBUTOR_NAME_TYPES);

    return convertArrayToMap(contributorNameTypes);
  }

  public static Map<String, JsonObjectWrapper> getAlternativeTitleTypes() {
    JSONArray contributorNameTypes = getJsonArray(
      readFileContentFromResources("mockData/inventory/get_alternative_title_types_response.json"),
      ALTERNATIVE_TITLE_TYPES);

    return convertArrayToMap(contributorNameTypes);
  }

  public static Map<String, JsonObjectWrapper> getLocations() {
    JSONArray locations = getJsonArray(
      readFileContentFromResources("mockData/inventory/get_locations_response.json"), LOCATIONS);

    return convertArrayToMap(locations);
  }

  public static Map<String, JsonObjectWrapper> getLoanTypes() {
    JSONArray loanTypes = getJsonArray(
      readFileContentFromResources("mockData/inventory/get_loan_types_response.json"), LOAN_TYPES);

    return convertArrayToMap(loanTypes);
  }

  public static Map<String, JsonObjectWrapper> getLibraries() {
    JSONArray libraries = getJsonArray(
      readFileContentFromResources("mockData/inventory/get_libraries_response.json"), LIBRARIES);

    return convertArrayToMap(libraries);
  }

  public static Map<String, JsonObjectWrapper> getCampuses() {
    JSONArray campus = getJsonArray(
      readFileContentFromResources("mockData/inventory/get_campuses_response.json"), CAMPUSES);

    return convertArrayToMap(campus);
  }

  public static Map<String, JsonObjectWrapper> getInstitutions() {
    JSONArray institutions = getJsonArray(
      readFileContentFromResources("mockData/inventory/get_institutions_response.json"),
      INSTITUTIONS);

    return convertArrayToMap(institutions);
  }

  public static Map<String, JsonObjectWrapper> getMaterialTypes() {
    JSONArray identifierTypes = getJsonArray(
      readFileContentFromResources("mockData/inventory/get_material_types_response.json"),
      MATERIAL_TYPES);

    return convertArrayToMap(identifierTypes);
  }

  public static Map<String, JsonObjectWrapper> getInstanceTypes() {
    JSONArray instanceTypes = getJsonArray(
      readFileContentFromResources("mockData/inventory/get_instance_types_response.json"),
      INSTANCE_TYPES);

    return convertArrayToMap(instanceTypes);
  }

  public static Map<String, JsonObjectWrapper> getInstanceFormats() {
    JSONArray instanceFormats = getJsonArray(
      readFileContentFromResources("mockData/inventory/get_instance_formats_response.json"),
      INSTANCE_FORMATS);

    return convertArrayToMap(instanceFormats);
  }

  public static Map<String, JsonObjectWrapper> getElectronicAccessRelationships() {
    JSONArray electronicAccessRelationships = getJsonArray(readFileContentFromResources(
      "mockData/inventory/get_electronic_access_relationships_response.json"),
      ELECTRONIC_ACCESS_RELATIONSHIPS);

    return convertArrayToMap(electronicAccessRelationships);
  }

  public static Map<String, JsonObjectWrapper> getModeOfIssuances() {
    JSONArray issuanceModes = getJsonArray(
      readFileContentFromResources("mockData/inventory/get_mode_of_issuance_response.json"),
      MODE_OF_ISSUANCES);

    return convertArrayToMap(issuanceModes);
  }

  public static Map<String, JsonObjectWrapper> getCallNumberTypes() {
    JSONArray callNumberTypes = getJsonArray(
      readFileContentFromResources("mockData/inventory/get_call_number_types_response.json"),
      CALL_NUMBER_TYPES);

    return convertArrayToMap(callNumberTypes);
  }

  private static Map<String, JsonObjectWrapper> convertArrayToMap(JSONArray array) {
    return array.stream()
      .collect(Collectors.toMap(key -> ((JSONObject) key).getAsString("id"),
        value -> new JsonObjectWrapper((JSONObject) value)));
  }

  private static JSONArray getJsonArray(String json, String key) {
    JSONObject jsonObject = JSONValue.parse(json, JSONObject.class);
    return (JSONArray) jsonObject.get(key);
  }

}
