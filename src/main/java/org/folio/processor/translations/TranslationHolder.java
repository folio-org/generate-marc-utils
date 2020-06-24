package org.folio.processor.translations;

public interface TranslationHolder {

  TranslationFunction lookup(String function);
}
