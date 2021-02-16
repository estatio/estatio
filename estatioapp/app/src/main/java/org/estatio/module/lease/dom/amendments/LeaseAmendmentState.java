package org.estatio.module.lease.dom.amendments;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum LeaseAmendmentState {
    APPLIED(true,false, Collections.emptyList()), // final state (immutable)
    REFUSED(true,false, Collections.emptyList()), // final state (immutable)
    APPLY(false,false, Arrays.asList(APPLIED)), // techinical state (reserved for bulk processing only)
    SIGNED(false,false, Arrays.asList(APPLY, APPLIED)), // formally approved but not yet technically implemented (immutable items)
    LITIGATION(false,true, Arrays.asList(SIGNED, REFUSED)), // waiting for outcome trial
    PROPOSED(false, true,Arrays.asList(SIGNED, REFUSED)), // under negotiation
    EXPECTED(false,true, Arrays.asList(PROPOSED, SIGNED, REFUSED)) // in case of co-ownership where no proposal yet
    ;

    public boolean isFinalState;
    public boolean isMutable;
    public List<LeaseAmendmentState> canTransitionTo;


    private LeaseAmendmentState(final boolean isFinalState, final boolean isMutable, final List<LeaseAmendmentState> canTransitionTo){
        this.isFinalState = isFinalState;
        this.isMutable = isMutable;
        this.canTransitionTo = canTransitionTo;
    }

}
