package org.estatio.dom.agreement;

import org.apache.isis.applib.annotation.MemberOrder;

import org.estatio.dom.WithReferenceContractTest_compareTo;
import org.estatio.dom.party.Party;

public class AgreementTest_compareTo extends WithReferenceContractTest_compareTo<Agreement> {

    protected Agreement newWithReference() {
        return new Agreement() {
            
            @Override
            @MemberOrder(sequence = "4")
            public Party getSecondaryParty() {
                return null;
            }
            
            @Override
            @MemberOrder(sequence = "3")
            public Party getPrimaryParty() {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }
}
