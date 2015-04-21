package org.estatio.dom.lease;

import org.estatio.dom.utils.MathUtils;

public enum IndexationMethod {
    BASE_INDEX {
        @Override
        public void doInitialze(LeaseTermForIndexable term) {
            if (term.getPrevious() != null) {
                term.setBaseIndexStartDate(((LeaseTermForIndexable) term.getPrevious()).getBaseIndexStartDate());
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
            if (term.getPrevious() != null) {
                term.setBaseIndexStartDate(((LeaseTermForIndexable) term.getPrevious()).getNextIndexStartDate());
            }
        }

        @Override
        public void doAlign(LeaseTermForIndexable term) {
            LeaseTermForIndexable previous = (LeaseTermForIndexable) term.getPrevious();
            if (previous != null) {
                term.setBaseValue(MathUtils.firstNonZero(
                        previous.getSettledValue(),
                        previous.getIndexedValue(),
                        previous.getBaseValue()));
            }
        }
    };

    public abstract void doInitialze(LeaseTermForIndexable term);

    public abstract void doAlign(LeaseTermForIndexable term);

}