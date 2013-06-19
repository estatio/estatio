package org.estatio.dom.agreement;

import org.junit.Assert;

import org.estatio.dom.party.Party;
import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.annotation.MemberOrder;

public class AgreementTest {

    private Agreement agreement;

    @Before
    public void setup() {
        agreement = new Agreement() {

            @Override
            @MemberOrder(sequence = "4")
            public Party getSecondaryParty() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            @MemberOrder(sequence = "3")
            public Party getPrimaryParty() {
                // TODO Auto-generated method stub
                return null;
            }
        };
        agreement.setStartDate(new LocalDate(2012, 1, 1));

    }

    @Test
    public void effectiveInterval() {
        Assert.assertNull(agreement.getEffectiveInterval().endDateExcluding());
        agreement.setTerminationDate(new LocalDate(2012, 6, 30));
        Assert.assertThat(agreement.getEffectiveInterval().endDateExcluding(), Is.is(new LocalDate(2012, 7, 1)));
    }

}
