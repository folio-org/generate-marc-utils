package org.folio.reader;

import lombok.extern.log4j.Log4j2;
import org.folio.processor.rule.DataSource;
import org.folio.processor.rule.Rule;
import org.folio.reader.values.MissingValue;
import org.folio.reader.values.RuleValue;

@SuppressWarnings("java:S3740")
@Log4j2
public abstract class AbstractEntityReader implements EntityReader {

  @Override
  public RuleValue read(Rule rule) {
    log.debug("read:: parameters rule: {}", rule);

    if (isSimpleRule(rule)) {
      return readSimpleValue(rule, rule.getDataSources().get(0));
    } else if (isCompositeRule(rule)) {
      return readCompositeValue(rule);
    }
    return MissingValue.getInstance();
  }

  private boolean isSimpleRule(Rule rule) {
    return rule.getDataSources().size() == 1;
  }

  private boolean isCompositeRule(Rule rule) {
    return rule.getDataSources().size() > 1;
  }

  protected abstract RuleValue readCompositeValue(Rule rule);

  protected abstract <S> S readSimpleValue(Rule rule, DataSource dataSource);
}
