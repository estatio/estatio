package org.estatio.dom.lease.indexation;

import java.math.BigDecimal;

import org.estatio.dom.index.Indexable;
import org.estatio.dom.lease.LeaseTermForIndexable;
import org.estatio.dom.lease.LeaseTermFrequency;
import org.incode.module.base.dom.utils.MathUtils;

public enum IndexationMethod {
    BASE_INDEX(false, false, true, IndexationCalculationMethod.DEFAULT),
    BASE_INDEX_ALLOW_DECREASE(true, true, true, IndexationCalculationMethod.DEFAULT),
    BASE_INDEX_NO_DECREASE_FRANCE(false, false, true, IndexationCalculationMethod.FRANCE),
    BASE_INDEX_ALLOW_DECREASE_BASE_AS_FLOOR_FRANCE(true, false, true, IndexationCalculationMethod.FRANCE),
    BASE_INDEX_ALLOW_DECREASE_FRANCE(true, true, true, IndexationCalculationMethod.FRANCE),
    LAST_KNOWN_INDEX(false, false, false, IndexationCalculationMethod.DEFAULT);

    private IndexationMethod(
            final boolean allowDecrease,
            final boolean allowDecreaseUnderBase,
            final boolean fixedBase,
            final IndexationCalculationMethod indexationCalculationMethod) {
        this.allowDecrease = allowDecrease;
        this.allowDecreaseUnderBase = allowDecreaseUnderBase;
        this.fixedBase = fixedBase;
        this.indexationCalculationMethod = indexationCalculationMethod;
    }

    private boolean allowDecrease;
    private boolean allowDecreaseUnderBase;
    private boolean fixedBase;
    private IndexationCalculationMethod indexationCalculationMethod;

    public IndexationCalculationMethod indexationCalculation() {
        return indexationCalculationMethod;
    }

    public void doInitialize(LeaseTermForIndexable term, Indexable previous) {
        if (previous != null) {
            LeaseTermFrequency frequency = term.getFrequency();
            if (fixedBase) {
                // Base value is copied on initialisation, never updated
                term.setBaseValue(previous.getBaseValue());
                term.setBaseIndexStartDate(previous.getBaseIndexStartDate());
                if (term.getFrequency() != null) {
                    term.setNextIndexStartDate(frequency.nextDate(previous.getNextIndexStartDate()));
                    term.setEffectiveDate(frequency.nextDate(previous.getEffectiveDate()));
                }
            } else {
                term.setBaseIndexStartDate(previous.getNextIndexStartDate());
                if (term.getFrequency() != null) {
                    term.setNextIndexStartDate(frequency.nextDate(previous.getNextIndexStartDate()));
                    term.setEffectiveDate(frequency.nextDate(previous.getEffectiveDate()));
                }
            }
        }
    }

    public void doAlignBeforeIndexation(Indexable term, Indexable previous) {
        if (previous != null && !fixedBase) {
            //base value changes when previous term have been changed
            term.setBaseValue(
                    MathUtils.firstNonZero(
                            previous.getSettledValue(),
                            MathUtils.maxUsingFirstSignum(
                                    previous.getBaseValue(),
                                    previous.getIndexedValue(),
                                    previous.getEffectiveIndexedValue())));
        }
    }

    // Decision tree for fixed base
    private fixedBaseScenario determineFixedBaseScenario(Indexable term, Indexable previous) {
        if (changeInBaseValue(term, previous)){ return fixedBaseScenario.CHANGED_BASE_VALUE; }
        if (allowDecrease && allowDecreaseUnderBase) { return fixedBaseScenario.ALLOW_DECREASE_UNDER_BASE; }
        if (allowDecrease && !allowDecreaseUnderBase) {return fixedBaseScenario.ALLOW_DECREASE_NOT_UNDER_BASE; }
        return fixedBaseScenario.NO_DECREASE;
    }

    public void doAlignAfterIndexation(Indexable term, Indexable previous) {

        if (fixedBase) {

            switch (determineFixedBaseScenario(term, previous)) {

            case CHANGED_BASE_VALUE:

                if (allowDecreaseUnderBase) {
                    term.setEffectiveIndexedValue(
                            MathUtils.firstNonZero(term.getIndexedValue(), term.getBaseValue())
                    );
                } else {
                    term.setEffectiveIndexedValue(
                            MathUtils.maxUsingFirstSignum(term.getIndexedValue(), term.getBaseValue())
                    );
                }
                break;

            case ALLOW_DECREASE_UNDER_BASE:
                term.setEffectiveIndexedValue(valueAllowingDecrease(term, previous));
                break;

            case ALLOW_DECREASE_NOT_UNDER_BASE:
                term.setEffectiveIndexedValue(
                        MathUtils.maxUsingFirstSignum(term.getBaseValue(),valueAllowingDecrease(term, previous))
                );
                break;

            case NO_DECREASE:
                term.setEffectiveIndexedValue(valueNotAllowingDecrease(term, previous));
                break;

            }

        } else {
            term.setEffectiveIndexedValue(
                    MathUtils.maxUsingFirstSignum(
                            term.getBaseValue(),
                            term.getIndexedValue(),
                            previous == null ? null : previous.getEffectiveIndexedValue()));
        }
    }

    private boolean changeInBaseValue(Indexable term, Indexable previous){
        if (previous != null && !term.getBaseValue().equals(previous.getBaseValue())){
            return true;
        }
        return false;
    }

    private BigDecimal valueAllowingDecrease(Indexable term, Indexable previous){
        return MathUtils.firstNonZero(
                term.getIndexedValue(),
                previous == null ? null : MathUtils.firstNonZero(previous.getEffectiveIndexedValue(), previous.getIndexedValue()),
                term.getBaseValue()
        );
    }

    private BigDecimal valueNotAllowingDecrease(Indexable term, Indexable previous){
        return MathUtils.maxUsingFirstSignum(
                term.getBaseValue(),
                term.getIndexedValue(),
                previous == null ? null : MathUtils.firstNonZero(previous.getEffectiveIndexedValue(), previous.getIndexedValue())
        );
    }

    private enum fixedBaseScenario {
        CHANGED_BASE_VALUE,
        ALLOW_DECREASE_UNDER_BASE,
        ALLOW_DECREASE_NOT_UNDER_BASE,
        NO_DECREASE
    }

}