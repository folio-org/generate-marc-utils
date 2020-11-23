package org.folio.reader;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import io.vertx.core.json.JsonObject;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.folio.processor.rule.DataSource;
import org.folio.processor.rule.Metadata;
import org.folio.processor.rule.Rule;
import org.folio.processor.error.RecordInfo;
import org.folio.processor.error.RecordType;
import org.folio.reader.values.CompositeValue;
import org.folio.reader.values.MissingValue;
import org.folio.reader.values.RuleValue;
import org.folio.reader.values.SimpleValue;
import org.folio.reader.values.StringValue;
import org.folio.reader.values.ValueWrapper;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.ordinalIndexOf;

/**
 * The implementation of {@link EntityReader} reads from JSON entity using JSONPath queries
 */
public class JPathSyntaxEntityReader extends AbstractEntityReader {
  private final DocumentContext documentContext;

  public JPathSyntaxEntityReader(JsonObject entity) {
    this.documentContext = JsonPath.parse(
      entity.encode(),
      Configuration.defaultConfiguration()
        .addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL)
        .addOptions(Option.SUPPRESS_EXCEPTIONS)
    );
  }

  @Override
  protected RuleValue readCompositeValue(Rule rule) {
    populateMetadata(rule);
    List<SimpleEntry<DataSource, List<ValueWrapper>>> matrix = readMatrix(rule);
    if (matrix.get(0).getValue().size() == 0) {
      return MissingValue.getInstance();
    } else {
      CompositeValue compositeValue = buildCompositeValue(matrix);
      applyReadDependingOnDataSourceFlag(compositeValue);
      return compositeValue;
    }
  }

  /**
   * Reads a matrix (two-dimensional array) of values by the given rule
   */
  private List<SimpleEntry<DataSource, List<ValueWrapper>>> readMatrix(Rule rule) {
    List<SimpleEntry<DataSource, List<ValueWrapper>>> matrix = new ArrayList<>();
    for (DataSource dataSource : rule.getDataSources()) {
      if (dataSource.getFrom() == null) {
        matrix.add(new SimpleEntry<>(dataSource, emptyList()));
      } else {
        Pair<String, RecordType> idPathType = buildIdPath(dataSource.getFrom());
        String idPath = idPathType.getKey();
        RecordType recordType = idPathType.getValue();
        Object idObject = this.documentContext.read(idPath);
        List<ValueWrapper> valueWrappers = new ArrayList<>();
        if (idObject instanceof String) {
          String recordId = (String) idObject;
          RecordInfo recordInfo = new RecordInfo(recordId, recordType);
          Object valueObject = this.documentContext.read(dataSource.getFrom());
          if (valueObject instanceof String) {
            valueWrappers.add(new ValueWrapper(recordInfo, valueObject));
          } else if (valueObject instanceof JSONArray) {
            ((JSONArray) valueObject).forEach(arrayValue -> {
              valueWrappers.add(new ValueWrapper(recordInfo, arrayValue));
            });
          }
        } else if (idObject instanceof JSONArray) {
          JSONArray ids = (JSONArray) idObject;
          for (Object id : ids) {
            String recordId = (String) id;
            RecordInfo recordInfo = new RecordInfo(recordId, recordType);
            Object valueObject = this.documentContext.read(buildIdentifiedPath(dataSource.getFrom(), recordInfo));
            if (valueObject instanceof String) {
              valueWrappers.add(new ValueWrapper(recordInfo, valueObject));
            } else if (valueObject instanceof JSONArray) {
              ((JSONArray) valueObject).forEach(arrayValue -> {
                valueWrappers.add(new ValueWrapper(recordInfo, arrayValue));
              });
            }
          }
        }
        matrix.add(new SimpleEntry<>(dataSource, valueWrappers));
      }
    }
    return matrix;
  }

  /**
   * Creates a composite value by the given matrix
   */
  private CompositeValue buildCompositeValue(List<SimpleEntry<DataSource, List<ValueWrapper>>> matrix) {
    CompositeValue compositeValue = new CompositeValue();
    int matrixWidth = matrix.get(0).getValue().size();
    for (int widthIndex = 0; widthIndex < matrixWidth; widthIndex++) {
      List<StringValue> entry = new ArrayList<>();
      for (SimpleEntry<DataSource, List<ValueWrapper>> field : matrix) {
        List<ValueWrapper> valueWrappers = field.getValue();
        DataSource dataSource = field.getKey();
        if (valueWrappers.isEmpty()) {
          entry.add(SimpleValue.ofNullable(dataSource));
        } else {
          if (valueWrappers.size() > widthIndex) {
            ValueWrapper valueWrapper = valueWrappers.get(widthIndex);
            Object object = valueWrapper.getValue();
            if (object instanceof String) {
              StringValue stringValue = SimpleValue.of((String) object, dataSource);
              stringValue.setRecordInfo(valueWrapper.getRecordInfo());
              entry.add(stringValue);
            } else if (valueWrapper.getValue() instanceof JSONArray) {
              JSONArray arrayValue = ((JSONArray) object);
              arrayValue.forEach(item -> {
                StringValue stringValue = SimpleValue.of((String) item, dataSource);
                stringValue.setRecordInfo(valueWrapper.getRecordInfo());
                entry.add(stringValue);
              });
            } else {
              entry.add(SimpleValue.of((String) object, dataSource));
            }
          } else {
            entry.add(SimpleValue.of((String) valueWrappers.get(0).getValue(), dataSource));
          }
        }
      }
      compositeValue.addEntry(entry);
    }
    return compositeValue;
  }

  /**
   * Applies 'readDependingOnDataSource' field to the given composite value
   */
  private void applyReadDependingOnDataSourceFlag(CompositeValue compositeValue) {
    compositeValue.getValue().removeIf(stringValues -> {
      for (StringValue stringValue : stringValues) {
        Integer dataSourceIndex = stringValue.getDataSource().getReadDependingOnDataSource();
        if (dataSourceIndex != null) {
          return stringValues.get(dataSourceIndex).getValue() == null;
        }
      }
      return false;
    });
  }

  @Override
  protected RuleValue readSimpleValue(Rule rule) {
    RuleValue ruleValue = MissingValue.getInstance();
    populateMetadata(rule);
    DataSource dataSource = rule.getDataSources().get(0);
    String path = dataSource.getFrom();
    Object readValue = documentContext.read(path);
    if (readValue instanceof String) {
      String string = (String) readValue;
      ruleValue = SimpleValue.of(string, dataSource);
      setRecordInfoToSimpleValue((SimpleValue) ruleValue);
    } else if (readValue instanceof JSONArray) {
      JSONArray array = (JSONArray) readValue;
      if (!array.isEmpty()) {
        if (array.get(0) instanceof String) {
          List<String> listOfStrings = new ArrayList<>();
          array.forEach(arrayStringItem -> listOfStrings.add(arrayStringItem.toString()));
          ruleValue = SimpleValue.of(listOfStrings, dataSource);
          setRecordInfoToSimpleValue((SimpleValue) ruleValue);
        } else if (array.get(0) instanceof Map) {
          throw new IllegalArgumentException(format("Reading a list of complex fields is not supported, data source: %s", dataSource));
        }
      }
    } else if (readValue instanceof Map) {
      throw new IllegalArgumentException(format("Reading a complex field is not supported, data source: %s", dataSource));
    }
    return ruleValue;
  }

  /**
   * Populates recordId to the given simple value
   */
  private void setRecordInfoToSimpleValue(SimpleValue simpleValue) {
    if (simpleValue.getDataSource().getFrom() != null) {
      Pair<String, RecordType> idPathType = buildIdPath(simpleValue.getDataSource().getFrom());
      String recordId = documentContext.read(idPathType.getKey());
      RecordType recordType = idPathType.getValue();
      simpleValue.setRecordInfo(new RecordInfo(recordId, recordType));
    }
  }

  /**
   * Reads up ans sets metadata to the given rule
   */
  private void populateMetadata(Rule rule) {
    Metadata metadata = rule.getMetadata();
    if (metadata != null) {
      for (Map.Entry<String, Metadata.Entry> entry : metadata.getData().entrySet()) {
        Object data = this.documentContext.read(entry.getValue().getFrom());
        if (data instanceof JSONArray) {
          JSONArray jsonArray = (JSONArray) data;
          List<?> list = Arrays.asList(jsonArray.toArray());
          entry.getValue().setData(list);
        }
      }
    }
  }

  /**
   * Builds path to the record id
   */
  private Pair<String, RecordType> buildIdPath(String valuePath) {
    String idPath = "$.instance.id";
    RecordType recordType = RecordType.INSTANCE;
    if (valuePath.contains("items")) {
      idPath = valuePath.substring(0, ordinalIndexOf(valuePath, "]", 2) + 1).concat(".id");
      recordType = RecordType.ITEM;
    } else if (valuePath.contains("holdings")) {
      idPath = valuePath.substring(0, ordinalIndexOf(valuePath, "]", 1) + 1).concat(".id");
      recordType = RecordType.HOLDING;
    }
    return new ImmutablePair<>(idPath, recordType);
  }

  /**
   * Builds jpath replacing the generic star composition ([*]) by the id of the concrete record
   */
  private String buildIdentifiedPath(String from, RecordInfo recordInfo) {
    String identifiedPath = from;
    String replacement = "?(@.id==\"" + recordInfo.getId() + "\")";
    if (recordInfo.getType().isHolding()) {
      identifiedPath = from.replaceFirst("[*]", replacement);
    } else if (recordInfo.getType().isItem()) {
      int index = ordinalIndexOf(from, "[", 2);
      identifiedPath = from.substring(0, index + 1) + replacement + from.substring(index + 2);
    }
    return identifiedPath;
  }
}
