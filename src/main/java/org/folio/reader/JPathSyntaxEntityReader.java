package org.folio.reader;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import io.vertx.core.json.JsonObject;
import net.minidev.json.JSONArray;
import org.folio.processor.rule.DataSource;
import org.folio.processor.rule.Metadata;
import org.folio.processor.rule.Rule;
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
      return compositeValue;
    }
  }

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
            if (object instanceof String) {
              String stringValue = (String) object;
              entry.add(SimpleValue.of(stringValue, field.getKey()));
            } else if (object instanceof JSONArray) {
              JSONArray arrayValue = ((JSONArray) object);
              String[] stringValues = Arrays.stream(arrayValue.toArray()).toArray(String[]::new);
              List<String> list = Arrays.asList(stringValues);
              for (String string : list) {
                entry.add(SimpleValue.of(string, field.getKey()));
              }
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
    populateMetadata(rule);
    DataSource dataSource = rule.getDataSources().get(0);
    String path = dataSource.getFrom();
    Object readValue = documentContext.read(path);
    if (readValue instanceof String) {
      String string = (String) readValue;
      return SimpleValue.of(string, dataSource);
    } else if (readValue instanceof JSONArray) {
      JSONArray array = (JSONArray) readValue;
      if (array.isEmpty()) {
        return MissingValue.getInstance();
      }
      if (array.get(0) instanceof String) {
        List<String> listOfStrings = new ArrayList<>();
        array.forEach(arrayItem -> listOfStrings.add(arrayItem.toString()));
        return SimpleValue.of(listOfStrings, dataSource);
      } else if (array.get(0) instanceof Map) {
        throw new IllegalArgumentException(format("Reading a list of complex fields is not supported, data source: %s", dataSource));
      }
    } else if (readValue instanceof Map) {
      throw new IllegalArgumentException(format("Reading a complex field is not supported, data source: %s", dataSource));
    }
    return MissingValue.getInstance();
  }
}
