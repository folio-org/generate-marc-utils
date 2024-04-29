package org.folio.reader;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.ordinalIndexOf;
import static org.folio.reader.values.SimpleValue.ofNullable;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.folio.processor.error.RecordInfo;
import org.folio.processor.error.RecordType;
import org.folio.processor.rule.DataSource;
import org.folio.processor.rule.Metadata;
import org.folio.processor.rule.Rule;
import org.folio.reader.values.CompositeValue;
import org.folio.reader.values.MissingValue;
import org.folio.reader.values.RuleValue;
import org.folio.reader.values.SimpleValue;
import org.folio.reader.values.StringValue;
import org.folio.reader.values.ValueWrapper;

/**
 * The implementation of {@link EntityReader} reads from JSON entity using JSONPath queries
 */
@Log4j2
public class JPathSyntaxEntityReader extends AbstractEntityReader {

  private final DocumentContext documentContext;

  public JPathSyntaxEntityReader(String json) {
    this.documentContext = JsonPath.parse(
      json,
      Configuration.defaultConfiguration()
        .addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL)
        .addOptions(Option.SUPPRESS_EXCEPTIONS)
    );
  }

  @Override
  protected RuleValue readSimpleValue(Rule rule, DataSource dataSource) {
    log.debug("readSimpleValue:: parameters rule field: {}", rule.getField());
    populateMetadata(rule);
    List<ValueWrapper> valueWrappers = readMatrix(rule).get(0).getValue();
    if (valueWrappers.isEmpty()) {
      return MissingValue.getInstance();
    } else {
      Optional<ValueWrapper> optionalNonNullValue = valueWrappers.stream()
        .filter(valueWrapper -> valueWrapper.getValue() != null).findFirst();
      if (optionalNonNullValue.isPresent()) {
        return buildSimpleValue(dataSource, valueWrappers, optionalNonNullValue.get().getValue());
      } else {
        return MissingValue.getInstance();
      }
    }
  }

  /**
   * Builds a SimpleValue, could be StringValue or ListValue
   */
  private SimpleValue<?> buildSimpleValue(DataSource dataSource, List<ValueWrapper> valueWrappers, Object nonNullValue) {
    SimpleValue<?> simpleValue;
    if (nonNullValue instanceof String && valueWrappers.size() == 1) {
      /* Building StringValue */
      ValueWrapper valueWrapper = valueWrappers.get(0);
      simpleValue = SimpleValue.of((String) valueWrapper.getValue(), dataSource, valueWrapper.getRecordInfo());
    } else {
      /* Building ListValue */
      List<StringValue> stringValues = new ArrayList<>();
      for (ValueWrapper valueWrapper : valueWrappers) {
        if (valueWrapper.getValue() instanceof String) {
          stringValues.add(SimpleValue.of((String) valueWrapper.getValue(), dataSource, valueWrapper.getRecordInfo()));
        } else if (valueWrapper.getValue() instanceof JSONArray) {
          ((JSONArray) valueWrapper.getValue()).forEach(arrayItem -> stringValues
            .add(SimpleValue.of(arrayItem.toString(), dataSource, valueWrapper.getRecordInfo())));
        } else if (valueWrapper.getValue() == null) {
          stringValues.add(ofNullable(dataSource));
        } else {
          log.error("Reading a complex values into a SimpleValue is not supported, data source: {}", dataSource);
          throw new IllegalArgumentException(
            format("Reading a complex values into a SimpleValue is not supported, data source: %s", dataSource));
        }
      }
      simpleValue = SimpleValue.of(stringValues, dataSource);
    }
    log.debug("buildSimpleValue:: result: {}", simpleValue);
    return simpleValue;
  }

  @Override
  protected RuleValue readCompositeValue(Rule rule) {
    log.debug("readCompositeValue:: parameters rule: {}", rule);
    populateMetadata(rule);
    List<SimpleEntry<DataSource, List<ValueWrapper>>> matrix = readMatrix(rule);
    if (isMatrixEmpty(matrix)) {
      return MissingValue.getInstance();
    } else {
      CompositeValue compositeValue = buildCompositeValue(matrix);
      applyReadIfFieldsNotEmpty(compositeValue);
      return compositeValue;
    }
  }

  /**
   * Checks whether there is at least one non-empty entry in the matrix and returns false
   * if found, otherwise true.
   */
  private boolean isMatrixEmpty(List<SimpleEntry<DataSource, List<ValueWrapper>>> matrix) {
    return matrix.stream().allMatch(entry -> entry.getValue().isEmpty());
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
            ((JSONArray) valueObject).forEach(arrayValue ->
              valueWrappers.add(new ValueWrapper(recordInfo, arrayValue)));
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
              ((JSONArray) valueObject).forEach(arrayValue ->
                valueWrappers.add(new ValueWrapper(recordInfo, arrayValue)));
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
    int matrixWidth = getProperMatrixWidth(matrix);
    for (int widthIndex = 0; widthIndex < matrixWidth; widthIndex++) {
      List<StringValue> entry = new ArrayList<>();
      for (SimpleEntry<DataSource, List<ValueWrapper>> field : matrix) {
        List<ValueWrapper> valueWrappers = field.getValue();
        DataSource dataSource = field.getKey();
        if (valueWrappers.isEmpty()) {
          entry.add(ofNullable(dataSource));
        } else {
          if (valueWrappers.size() > widthIndex) {
            ValueWrapper valueWrapper = tryToGetValueWrapperForHoldingsStatements(valueWrappers, entry)
              .orElse(valueWrappers.get(widthIndex));
            Object object = valueWrapper.getValue();
            if (object instanceof String) {
              StringValue stringValue = SimpleValue.of((String) object, dataSource, valueWrapper.getRecordInfo());
              stringValue.setRecordInfo(valueWrapper.getRecordInfo());
              entry.add(stringValue);
            } else if (valueWrapper.getValue() instanceof JSONArray) {
              JSONArray arrayValue = ((JSONArray) object);
              arrayValue.forEach(item -> {
                StringValue stringValue = SimpleValue.of((String) item, dataSource, valueWrapper.getRecordInfo());
                stringValue.setRecordInfo(valueWrapper.getRecordInfo());
                entry.add(stringValue);
              });
            } else {
              entry.add(SimpleValue.of(nonNull(object) ? (String)object : null, dataSource, valueWrapper.getRecordInfo()));
            }
          } else {
            entry.add(SimpleValue.of((String) tryToGetValueWrapperForHoldingsStatements(valueWrappers, entry)
              .orElse(valueWrappers.get(0)).getValue(), dataSource, tryToGetValueWrapperForHoldingsStatements(valueWrappers, entry)
              .orElse(valueWrappers.get(0)).getRecordInfo()));
          }
        }
      }
      removeHoldingNotesFromEntryIfNotBelongToHolding(entry);
      compositeValue.addEntry(entry);
    }
    return compositeValue;
  }

  private int getProperMatrixWidth(List<SimpleEntry<DataSource, List<ValueWrapper>>> matrix) {
    return matrix.stream()
      .mapToInt(dataSourceListSimpleEntry -> dataSourceListSimpleEntry.getValue().size()).max()
      .orElse(0);
  }

  /**
   * Applies 'readDependingOnDataSource' field to the given composite value
   */
  private void applyReadIfFieldsNotEmpty(CompositeValue compositeValue) {
    compositeValue.getValue().removeIf(stringValues -> isItemTypeValues(stringValues) && allRequiredItemFieldsAreEmpty(stringValues));
  }

  private boolean isItemTypeValues(List<StringValue> stringValues) {
    return stringValues.stream().map(stringValue -> stringValue.getDataSource().getFrom())
      .filter(StringUtils::isNoneEmpty)
      .anyMatch(from -> from.contains(".items"));
  }

  /**
   * Checks if any one item field that should be mapped in scope of a single composite value has a value.
   * StringValue with holding hrId is not involved since always has a value.
   *
   * @param stringValues - item string values to verify
   * @return true if all related item fields are empty
   */
  private boolean allRequiredItemFieldsAreEmpty(List<StringValue> stringValues) {
    return stringValues.subList(0, stringValues.size() - 1).stream()
      .map(StringValue::getValue)
      .allMatch(Objects::isNull);
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

  private Optional<ValueWrapper> tryToGetValueWrapperForHoldingsStatements(List<ValueWrapper> valueWrappers, List<StringValue> entry) {
    if (entry.isEmpty()) {
      return Optional.empty();
    }
    var res = valueWrappers.stream().filter(valueWrapper -> isHoldingsStatement(valueWrapper, entry)).toList();
    if (res.size() != 1) {
      return Optional.empty();
    }
    return Optional.of(res.get(0));
  }

  private boolean isHoldingsStatement(ValueWrapper valueWrapper, List<StringValue> entry) {
    return entry.get(0).getDataSource().getFrom().equals("$.holdings[*].holdingsStatements[*].statement") &&
      nonNull(entry.get(0).getRecordInfo()) &&
      valueWrapper.getRecordInfo().getId().equals(entry.get(0).getRecordInfo().getId());
  }

  private void removeHoldingNotesFromEntryIfNotBelongToHolding(List<StringValue> entry) {
    if (!entry.isEmpty()) {
      entry.removeIf(val -> nonNull(entry.get(0).getRecordInfo()) && nonNull(val.getRecordInfo()) &&
        val.getDataSource().getFrom().startsWith("$.holdings[*].notes") &&
        !val.getRecordInfo().getId().equals(entry.get(0).getRecordInfo().getId()));
    }
  }
}
