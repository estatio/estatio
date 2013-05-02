package org.estatio.dom.lease;

import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

public class LeaseTermFrequencyTest {

    @Test
    public void testYearly() {
        Assert.assertThat(LeaseTermFrequency.YEARLY.nextDate(new LocalDate(2012,1,1)), Is.is(new LocalDate(2013,1,1)));
        Assert.assertThat(LeaseTermFrequency.YEARLY.nextDate(new LocalDate(2012,4,15)), Is.is(new LocalDate(2013,4,15)));
    }

    @Test
    public void testNoFrequency() {
        Assert.assertNull(LeaseTermFrequency.NO_FREQUENCY.nextDate(new LocalDate(2012,1,1)));
        Assert.assertNull(LeaseTermFrequency.NO_FREQUENCY.nextDate(new LocalDate(2012,4,15)));
    }

}
