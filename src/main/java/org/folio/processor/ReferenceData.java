package org.folio.processor;

import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

public interface ReferenceData {

  Map<String, JsonObject> getByKey(String referenceDataKey);
}
