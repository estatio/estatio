package org.estatio.dom.agreement;

import org.estatio.dom.WithIntervalContractTest_getInterval;
import org.estatio.dom.party.Party;

public class AgreementTest_getInterval extends WithIntervalContractTest_getInterval<Agreement> {

    protected Agreement newWithInterval() {
        return new Agreement() {
            
            @Override
            public Party getSecondaryParty() {
                return null;
            }
            
            @Override
            public Party getPrimaryParty() {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }
}
