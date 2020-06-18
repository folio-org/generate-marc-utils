package org.folio;

import io.vertx.core.json.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public final class TestUtil {
  public static String readFileContentFromResources(String path) {
    try {
      ClassLoader classLoader = TestUtil.class.getClassLoader();
      URL url = classLoader.getResource(path);
      return IOUtils.toString(url, StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalStateException(e);
    }
  }

  public static File getFileFromResources(String path) {
    ClassLoader classLoader = TestUtil.class.getClassLoader();
    return new File(Objects.requireNonNull(classLoader.getResource(path)).getFile());
  }

  public static String readFileContent(String path) {
    try {
      return FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalStateException(e);
    }
  }

  public static Map<String, JsonObject> getNatureOfContentTerms() {
    JsonObject natureOfContentTerm =
      new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_nature_of_content_terms_response.json"))
        .getJsonArray("natureOfContentTerms")
        .getJsonObject(0);
    return Collections.singletonMap(natureOfContentTerm.getString("id"), natureOfContentTerm);
  }

  public static Map<String, JsonObject> getIdentifierTypes() {
    JsonObject identifierType =
      new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_identifier_types_response.json"))
        .getJsonArray("identifierTypes")
        .getJsonObject(0);
    return Collections.singletonMap(identifierType.getString("id"), identifierType);
  }

  public static Map<String, JsonObject> getContributorNameTypes() {
    JsonObject contributorNameTypes =
      new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_contributor_name_types_response.json"))
        .getJsonArray("contributorNameTypes")
        .getJsonObject(0);
    return Collections.singletonMap(contributorNameTypes.getString("id"), contributorNameTypes);
  }

  public static Map<String, JsonObject> getLocations() {
    JsonObject identifierType =
      new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_locations_response.json"))
        .getJsonArray("locations")
        .getJsonObject(0);
    return Collections.singletonMap(identifierType.getString("id"), identifierType);
  }

  public static Map<String, JsonObject> getMaterialTypes() {
    JsonObject identifierType =
      new JsonObject(TestUtil.readFileContentFromResources("mockData/inventory/get_material_types_response.json"))
        .getJsonArray("mtypes")
        .getJsonObject(0);
    return Collections.singletonMap(identifierType.getString("id"), identifierType);
  }
}
