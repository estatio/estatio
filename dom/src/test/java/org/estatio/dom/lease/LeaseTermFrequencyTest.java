package org.estatio.dom.lease;

import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

public class LeaseTermFrequencyTest {

    @Test
    public void testYearly() {
        Assert.assertThat(LeaseTermFrequency.YEARLY.nextDate(new LocalDate(2012,1,1)), Is.is(new LocalDate(2013,1,1)));
    }
}
