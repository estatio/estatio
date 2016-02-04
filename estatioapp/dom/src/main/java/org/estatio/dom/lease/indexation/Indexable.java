package org.estatio.dom.lease.indexation;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.estatio.dom.index.Index;
import org.estatio.dom.lease.LeaseTermFrequency;

public interface Indexable {

    LocalDate getBaseIndexStartDate();

    BigDecimal getBaseIndexValue();

    BigDecimal getBaseValue();

    Index getIndex();

    BigDecimal getIndexationPercentage();

    BigDecimal getLevellingPercentage();

    LocalDate getNextIndexStartDate();

    BigDecimal getNextIndexValue();

    BigDecimal getRebaseFactor();

    void setBaseIndexStartDate(LocalDate baseIndexStartDate);

    void setBaseIndexValue(BigDecimal baseIndexValue);

    void setIndex(Index index);

    void setIndexationPercentage(BigDecimal indexationPercentage);

    void setIndexedValue(BigDecimal indexedValue);

    void setNextIndexStartDate(LocalDate nextIndexStartDate);

    void setNextIndexValue(BigDecimal nextIndexValue);

    void setRebaseFactor(BigDecimal rebaseFactor);

    BigDecimal getIndexedValue();

    void setBaseValue(BigDecimal baseValue);

    LeaseTermFrequency getFrequency();

    LocalDate getEffectiveDate();

    void setEffectiveDate(LocalDate localDate);

    BigDecimal getEffectiveIndexedValue();

    void setEffectiveIndexedValue(BigDecimal max);

    BigDecimal getSettledValue();
}
