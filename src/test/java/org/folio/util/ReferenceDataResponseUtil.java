package org.folio.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ReferenceDataResponseUtil {

  public static Map<String, JsonObject> getNatureOfContentTerms() {
    JsonArray natureOfContentTerm =
      new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_nature_of_content_terms_response.json"))
        .getJsonArray("natureOfContentTerms");

    return natureOfContentTerm.stream()
      .collect(Collectors.toMap(key -> new JsonObject(key.toString()).getString("id"), val -> new JsonObject(val.toString())));
  }

  public static Map<String, JsonObject> getIdentifierTypes() {
    JsonArray identifierType =
      new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_identifier_types_response.json"))
        .getJsonArray("identifierTypes");

    return identifierType.stream()
      .collect(Collectors.toMap(key -> new JsonObject(key.toString()).getString("id"), val -> new JsonObject(val.toString())));
  }

  public static Map<String, JsonObject> getContributorNameTypes() {
    JsonArray contributorNameTypes =
      new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_contributor_name_types_response.json"))
        .getJsonArray("contributorNameTypes");

    return contributorNameTypes.stream()
      .collect(Collectors.toMap(key -> new JsonObject(key.toString()).getString("id"), val -> new JsonObject(val.toString())));
  }

  public static Map<String, JsonObject> getLocations() {
    JsonArray locations =
      new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_locations_response.json"))
        .getJsonArray("locations");
    return locations.stream()
      .collect(Collectors.toMap(key -> new JsonObject(key.toString()).getString("id"), val -> new JsonObject(val.toString())));
  }


  public static Map<String, JsonObject> getLoanTypes() {
    JsonArray loanType = new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_loan_types_response.json"))
      .getJsonArray("loantypes");
    return loanType.stream()
      .collect(Collectors.toMap(key -> new JsonObject(key.toString()).getString("id"), val -> new JsonObject(val.toString())));
  }

  public static Map<String, JsonObject> getLibraries() {
    JsonArray libraries =
      new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_libraries_response.json"))
        .getJsonArray("loclibs");

    return libraries.stream()
      .collect(Collectors.toMap(key -> new JsonObject(key.toString()).getString("id"), val -> new JsonObject(val.toString())));
  }

  public static Map<String, JsonObject> getCampuses() {
    JsonArray campus =
      new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_campuses_response.json"))
        .getJsonArray("loccamps");

    return campus.stream()
      .collect(Collectors.toMap(key -> new JsonObject(key.toString()).getString("id"), val -> new JsonObject(val.toString())));
  }

  public static Map<String, JsonObject> getInstitutions() {
    JsonArray institutions =
      new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_institutions_response.json"))
        .getJsonArray("locinsts");

    return institutions.stream()
      .collect(Collectors.toMap(key -> new JsonObject(key.toString()).getString("id"), val -> new JsonObject(val.toString())));
  }

  public static Map<String, JsonObject> getMaterialTypes() {
    JsonArray identifierType =
      new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_material_types_response.json"))
        .getJsonArray("mtypes");

    return identifierType.stream()
      .collect(Collectors.toMap(key -> new JsonObject(key.toString()).getString("id"), val -> new JsonObject(val.toString())));
  }

  public static Map<String, JsonObject> getInstanceTypes() {
    JsonArray instanceTypes =
      new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_instance_types_response.json"))
        .getJsonArray("instanceTypes");

    return instanceTypes.stream()
      .collect(Collectors.toMap(key -> new JsonObject(key.toString()).getString("id"), val -> new JsonObject(val.toString())));
  }

  public static Map<String, JsonObject> getInstanceFormats() {
    Map<String, JsonObject> stringJsonObjectMap = new HashMap<>();
    JsonArray instanceFormats =
      new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_instance_formats_response.json"))
        .getJsonArray("instanceFormats");
    instanceFormats.stream().forEach(instanceFormat -> {
      JsonObject jsonObject = new JsonObject(instanceFormat.toString());
      stringJsonObjectMap.put(jsonObject.getString("id"), jsonObject);
    });
    return stringJsonObjectMap;
  }

  public static Map<String, JsonObject> getElectronicAccessRelationships() {
    Map<String, JsonObject> stringJsonObjectMap = new HashMap<>();
    JsonArray electronicAccessRelationships =
      new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_electronic_access_relationships_response.json"))
        .getJsonArray("electronicAccessRelationships");
    electronicAccessRelationships.stream().forEach(electronicAccessRelationship -> {
      JsonObject jsonObject = new JsonObject(electronicAccessRelationship.toString());
      stringJsonObjectMap.put(jsonObject.getString("id"), jsonObject);
    });
    return stringJsonObjectMap;
  }

  public static Map<String, JsonObject> getModeOfIssuances() {
    JsonArray issuanceModes =
      new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_mode_of_issuance_response.json"))
        .getJsonArray("issuanceModes");

    return issuanceModes.stream()
      .collect(Collectors.toMap(key -> new JsonObject(key.toString()).getString("id"), val -> new JsonObject(val.toString())));
  }

  public static Map<String, JsonObject> getCallNumberTypes() {
    JsonArray callNumberTypes =
      new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_call_number_types_response.json"))
        .getJsonArray("callNumberTypes");

    return callNumberTypes.stream()
      .collect(Collectors.toMap(key -> new JsonObject(key.toString()).getString("id"), val -> new JsonObject(val.toString())));
  }

}
