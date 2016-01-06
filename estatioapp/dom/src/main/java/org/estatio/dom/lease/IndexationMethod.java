package org.estatio.dom.lease;

public enum IndexationMethod {
    BASE_INDEX {
        @Override
        public void doInitialze(LeaseTermForIndexable term) {
            final LeaseTermForIndexable previous = (LeaseTermForIndexable) term.getPrevious();
            if (previous != null) {
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
        public void doAlign(LeaseTermForIndexable term) {
            LeaseTermForIndexable previous = (LeaseTermForIndexable) term.getPrevious();
            if (previous != null) {
                term.setBaseValue(previous.getBaseValue());
            }
        }
    },
    LAST_KNOWN_INDEX {
        @Override
        public void doInitialze(LeaseTermForIndexable term) {
            final LeaseTermForIndexable previous = (LeaseTermForIndexable) term.getPrevious();
            if (previous != null) {
                term.setBaseValue(previous.getEffectiveValue());
                LeaseTermFrequency frequency = term.getFrequency();
                term.setBaseIndexStartDate(previous.getNextIndexStartDate());
                if (term.getFrequency() != null) {
                    term.setNextIndexStartDate(frequency.nextDate(previous.getNextIndexStartDate()));
                    term.setEffectiveDate(frequency.nextDate(previous.getEffectiveDate()));
                }
            }
        }

        @Override
        public void doAlign(LeaseTermForIndexable term) {
            LeaseTermForIndexable previous = (LeaseTermForIndexable) term.getPrevious();
            if (previous != null && term.getBaseValue() == null) {
                term.setBaseValue(previous.getEffectiveValue());
            }
        }
    };

    public abstract void doInitialze(LeaseTermForIndexable term);

    public abstract void doAlign(LeaseTermForIndexable term);

}