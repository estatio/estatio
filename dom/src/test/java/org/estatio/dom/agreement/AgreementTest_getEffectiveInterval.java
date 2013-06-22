package org.estatio.dom.agreement;

import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AgreementTest_getEffectiveInterval {

    private Agreement agreement;

    @Before
    public void setup() {
        agreement = new AgreementForTesting();
        agreement.setStartDate(new LocalDate(2012, 1, 1));

    }

    @Test
    public void getEffectiveInterval() {
        Assert.assertNull(agreement.getEffectiveInterval().endDateExcluding());
        agreement.setTerminationDate(new LocalDate(2012, 6, 30));
        Assert.assertThat(agreement.getEffectiveInterval().endDateExcluding(), Is.is(new LocalDate(2012, 7, 1)));
    }

}
