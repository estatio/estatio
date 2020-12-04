package org.estatio.module.lease.dom.amendments;

public enum LeaseAmendmentState {
    PROPOSED, // under negotiation
    SIGNED, // formally approved but not yet technically implemented (immutable items)
    APPLY, // techinical state (reserved for bulk processing only)
    APPLIED // final state (immutable)
}
