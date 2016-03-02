package org.estatio.dom.lease.indexation;

import java.math.BigDecimal;

/**
 * Created by jvanderwal on 01/03/16.
 */
class IndexationResult {

    public static final IndexationResult NULL = new IndexationResult(null, null, null, null);

    private BigDecimal indexedValue = null;
    private BigDecimal indexationPercentage = null;
    private BigDecimal baseIndexValue = null;
    private BigDecimal nextIndexValue = null;

    public IndexationResult(
            final BigDecimal indexedValue,
            final BigDecimal indexationPercentage,
            final BigDecimal baseIndexValue,
            final BigDecimal nextIndexValue) {
        this.indexedValue = indexedValue;
        this.indexationPercentage = indexationPercentage;
        this.baseIndexValue = baseIndexValue;
        this.nextIndexValue = nextIndexValue;
    }

    public void apply(final Indexable indexable) {
        indexable.setBaseIndexValue(baseIndexValue);
        indexable.setIndexationPercentage(indexationPercentage);
        indexable.setNextIndexValue(nextIndexValue);
        indexable.setIndexedValue(indexedValue);
        indexable.setIndexedValue(
                // Don't apply when negative indexation.
                // Probably configurable in the future
                indexationPercentage != null && indexationPercentage.compareTo(BigDecimal.ZERO) < 0 ?
                        indexable.getBaseValue() :
                        indexedValue);
    }
}
