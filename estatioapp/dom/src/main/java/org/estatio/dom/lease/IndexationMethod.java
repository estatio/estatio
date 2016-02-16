package org.estatio.dom.lease;

import org.estatio.dom.lease.indexation.Indexable;
import org.estatio.dom.utils.MathUtils;

public enum IndexationMethod {
    BASE_INDEX {
        @Override
        public void doInitialze(Indexable term, Indexable previous) {
            if (previous != null) {
                // Base value is copied on initialisation, never updated
                term.setBaseValue(previous.getBaseValue());
                LeaseTermFrequency frequency = term.getFrequency();
                term.setBaseIndexStartDate(previous.getBaseIndexStartDate());
                if (term.getFrequency() != null) {
                    term.setNextIndexStartDate(frequency.nextDate(previous.getNextIndexStartDate()));
                    term.setEffectiveDate(frequency.nextDate(previous.getEffectiveDate()));
                }
            }
        }

        @Override
        public void doAlignBeforeIndexation(final Indexable term, final Indexable previous) {
            // Nothing to do before with base indexe methond
        }

        @Override
        public void doAlignAfterIndexation(final Indexable term, final Indexable previous) {
            term.setEffectiveIndexedValue(
                    MathUtils.max(
                            term.getBaseValue(),
                            term.getIndexedValue(),
                            previous == null ? null : previous.getEffectiveIndexedValue()));
        }
    },
    BASE_INDEX_ALLOW_DECREASE {
        @Override
        public void doInitialze(Indexable term, Indexable previous) {
            if (previous != null) {
                // Base value is copied on initialisation, never updated
                term.setBaseValue(previous.getBaseValue());
                LeaseTermFrequency frequency = term.getFrequency();
                term.setBaseIndexStartDate(previous.getBaseIndexStartDate());
                if (term.getFrequency() != null) {
                    term.setNextIndexStartDate(frequency.nextDate(previous.getNextIndexStartDate()));
                    term.setEffectiveDate(frequency.nextDate(previous.getEffectiveDate()));
                }
            }
        }

        @Override
        public void doAlignBeforeIndexation(final Indexable term, final Indexable previous) {
            // Nothing to do before with base indexe methond
        }

        @Override
        public void doAlignAfterIndexation(final Indexable term, final Indexable previous) {
            term.setEffectiveIndexedValue(
                    MathUtils.firstNonZero(
                            term.getIndexedValue(),
                            previous == null ? null : previous.getEffectiveIndexedValue(),
                            term.getBaseValue()));
        }
    },

    LAST_KNOWN_INDEX {
        @Override
        public void doInitialze(Indexable term, Indexable previous) {
            if (previous != null) {
                LeaseTermFrequency frequency = term.getFrequency();
                term.setBaseIndexStartDate(previous.getNextIndexStartDate());
                if (term.getFrequency() != null) {
                    term.setNextIndexStartDate(frequency.nextDate(previous.getNextIndexStartDate()));
                    term.setEffectiveDate(frequency.nextDate(previous.getEffectiveDate()));
                }
            }
        }

        @Override
        public void doAlignBeforeIndexation(Indexable term, Indexable previous) {
            if (previous != null) {
                //base value changes when previous term have been changed
                term.setBaseValue(
                        MathUtils.firstNonZero(
                                previous.getSettledValue(),
                                MathUtils.max(
                                        previous.getBaseValue(),
                                        previous.getIndexedValue(),
                                        previous.getEffectiveIndexedValue())));
            }
        }

        @Override
        public void doAlignAfterIndexation(final Indexable term, final Indexable previous) {
            if (previous != null) {
                term.setEffectiveIndexedValue(
                        MathUtils.firstNonZero(
                                previous.getSettledValue(),
                                MathUtils.max(
                                        term.getBaseValue(),
                                        term.getIndexedValue(),
                                        previous.getEffectiveIndexedValue())));
            }
        }
    };

    public abstract void doInitialze(Indexable term, Indexable previous);

    public abstract void doAlignBeforeIndexation(Indexable term, Indexable previous);

    public abstract void doAlignAfterIndexation(Indexable term, Indexable previous);

}