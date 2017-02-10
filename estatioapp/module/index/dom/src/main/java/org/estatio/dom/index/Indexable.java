package org.estatio.dom.index;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.estatio.dom.index.indexation.IndexationCalculationMethod;

public interface Indexable {

    LocalDate getBaseIndexStartDate();

    void setBaseIndexStartDate(LocalDate baseIndexStartDate);

    BigDecimal getBaseIndexValue();

    void setBaseIndexValue(BigDecimal baseIndexValue);

    BigDecimal getBaseValue();

    void setBaseValue(BigDecimal baseValue);

    Index getIndex();

    void setIndex(Index index);

    BigDecimal getIndexationPercentage();

    void setIndexationPercentage(BigDecimal indexationPercentage);

    BigDecimal getLevellingPercentage();

    LocalDate getNextIndexStartDate();

    void setNextIndexStartDate(LocalDate nextIndexStartDate);

    BigDecimal getNextIndexValue();

    void setNextIndexValue(BigDecimal nextIndexValue);

    BigDecimal getRebaseFactor();

    void setRebaseFactor(BigDecimal rebaseFactor);

    BigDecimal getIndexedValue();

    void setIndexedValue(BigDecimal indexedValue);

//    LeaseTermFrequency getFrequency();

    LocalDate getEffectiveDate();

    void setEffectiveDate(LocalDate localDate);

    BigDecimal getEffectiveIndexedValue();

    void setEffectiveIndexedValue(BigDecimal max);

    BigDecimal getSettledValue();

    IndexationCalculationMethod getIndexationCalculation();
}
