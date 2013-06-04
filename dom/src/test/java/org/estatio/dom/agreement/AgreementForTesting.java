package org.estatio.dom.agreement;

import org.apache.isis.applib.annotation.MemberOrder;

import org.estatio.dom.party.Party;

public class AgreementForTesting extends Agreement {

    @Override
    public Party getPrimaryParty() {
        return null;
    }

    @Override
    public Party getSecondaryParty() {
        return null;
    }
}