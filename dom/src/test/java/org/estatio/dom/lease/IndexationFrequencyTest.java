package org.estatio.dom.lease;

import junit.framework.Assert;

import org.estatio.dom.lease.IndexationFrequency;
import org.joda.time.LocalDate;
import org.junit.Test;

public class IndexationFrequencyTest {

    @Test
    public void testYearly() {
        Assert.assertEquals(new LocalDate(2013,1,1), IndexationFrequency.YEARLY.nextDate(new LocalDate(2012,1,1)));
    }

}
