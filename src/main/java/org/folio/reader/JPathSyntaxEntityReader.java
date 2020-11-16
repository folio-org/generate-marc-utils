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
import org.folio.reader.record.RecordInfo;
import org.folio.reader.record.RecordType;
import org.folio.reader.values.CompositeValue;
import org.folio.reader.values.MissingValue;
import org.folio.reader.values.RuleValue;
import org.folio.reader.values.SimpleValue;
import org.folio.reader.values.StringValue;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
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
    List<SimpleEntry<DataSource, JSONArray>> matrix = readMatrix(rule);
    if (matrix.get(0).getValue().size() == 0) {
      return MissingValue.getInstance();
    } else {
      CompositeValue compositeValue = buildCompositeValue(matrix);
      applyReadDependingOnDataSourceFlag(compositeValue);
      setRecordInfoToCompositeValue(compositeValue);
      return compositeValue;
    }
  }

  /**
   * Reads a matrix (two-dimensional array) of values by the given rule
   *
   * @param rule mapping rule
   * @return matrix
   */
  private List<SimpleEntry<DataSource, JSONArray>> readMatrix(Rule rule) {
    List<SimpleEntry<DataSource, JSONArray>> matrix = new ArrayList<>();
    for (DataSource dataSource : rule.getDataSources()) {
      if (dataSource.getFrom() == null) {
        matrix.add(new SimpleEntry<>(dataSource, new JSONArray()));
      } else {
        Object objectValue = this.documentContext.read(dataSource.getFrom());
        if (objectValue instanceof String) {
          String stringValue = (String) objectValue;
          matrix.add(new SimpleEntry<>(dataSource, new JSONArray().appendElement(stringValue)));
        } else if (objectValue instanceof JSONArray) {
          JSONArray value = (JSONArray) objectValue;
          matrix.add(new SimpleEntry<>(dataSource, value));
        }
      }
    }
    return matrix;
  }

  /**
   * Creates a composite value by the given matrix
   *
   * @param matrix two-dimensional array of values
   * @return composite value
   */
  private CompositeValue buildCompositeValue(List<SimpleEntry<DataSource, JSONArray>> matrix) {
    CompositeValue compositeValue = new CompositeValue();
    int matrixLength = matrix.size();
    int matrixWidth = matrix.get(0).getValue().size();
    for (int widthIndex = 0; widthIndex < matrixWidth; widthIndex++) {
      List<StringValue> entry = new ArrayList<>();
      for (int lengthIndex = 0; lengthIndex < matrixLength; lengthIndex++) {
        SimpleEntry<DataSource, JSONArray> field = matrix.get(lengthIndex);
        JSONArray jsonArray = field.getValue();
        if (jsonArray.isEmpty()) {
          entry.add(SimpleValue.ofNullable(field.getKey()));
        } else {
          if (jsonArray.size() > widthIndex) {
            Object object = jsonArray.get(widthIndex);
            DataSource dataSource = field.getKey();
            if (object instanceof String) {
              String stringValue = (String) object;
              entry.add(SimpleValue.of(stringValue, dataSource));
            } else if (object instanceof JSONArray) {
              JSONArray arrayValue = ((JSONArray) object);
              String[] stringValues = Arrays.stream(arrayValue.toArray()).toArray(String[]::new);
              List<String> list = Arrays.asList(stringValues);
              for (String string : list) {
                entry.add(SimpleValue.of(string, dataSource));
              }
            } else {
              entry.add(SimpleValue.of((String) object, dataSource));
            }
          } else {
            entry.add(SimpleValue.of((String) jsonArray.get(0), field.getKey()));
          }
        }
      }
      compositeValue.addEntry(entry);
    }
    return compositeValue;
  }

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
   *
   * @param simpleValue simple value
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
   * Populates recordId to the given composite value
   *
   * @param compositeValue composite value
   */
  private void setRecordInfoToCompositeValue(CompositeValue compositeValue) {
    for (List<StringValue> stringValues : compositeValue.getValue()) {
      Pair<String, RecordType> idPathType = buildIdPath(stringValues.get(0).getDataSource().getFrom());
      String idPath = idPathType.getKey();
      RecordType recordType = idPathType.getValue();
      Object idObject = documentContext.read(idPath);
      if (idObject instanceof String) {
        String recordId = (String) idObject;
        RecordInfo recordInfo = new RecordInfo(recordId, recordType);
        for (StringValue stringValue : stringValues) {
          stringValue.setRecordInfo(recordInfo);
        }
      } else if (idObject instanceof JSONArray) {
        JSONArray ids = (JSONArray) idObject;
        for (int i = 0; i < stringValues.size(); i++) {
          String recordId = (String) ids.get(i);
          stringValues.get(i).setRecordInfo(new RecordInfo(recordId, recordType));
        }
      }
    }
  }

  /**
   * Builds path to the record id
   *
   * @param valuePath path for the record value
   * @return  id path
   */
  private Pair<String, RecordType> buildIdPath(String valuePath) {
    String idPath = "$.id";
    RecordType recordType = RecordType.INSTANCE;
    if (valuePath.contains("holdings")) {
      idPath = valuePath.substring(0, ordinalIndexOf(valuePath, "]", 1) + 1).concat(".id");
      recordType = RecordType.HOLDING;
    } else if (valuePath.contains("items")) {
      idPath = valuePath.substring(0, ordinalIndexOf(valuePath, "]", 2) + 1).concat(".id");
      recordType = RecordType.ITEM;
    }
    return new ImmutablePair<>(idPath, recordType);
  }

}
