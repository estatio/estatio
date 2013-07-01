package org.estatio.dom.agreement;

import org.estatio.dom.Status;
import org.estatio.dom.party.Party;

public class AgreementForTesting extends Agreement<Status> {

    public AgreementForTesting() {
        super(null, null);
    }

    @Override
    public Party getPrimaryParty() {
        return null;
    }

    @Override
    public Party getSecondaryParty() {
        return null;
    }

    @Override
    public Status getStatus() {
        return null;
    }

    @Override
    public void setStatus(Status newStatus) {
    }

}