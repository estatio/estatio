package org.estatio.dom.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;
import org.junit.Test;

public class LeaseTermFrequencyTest_nextDate {

    @Test
    public void whenYearly() {
        assertThat(LeaseTermFrequency.YEARLY.nextDate(new LocalDate(2012,1,1)), is(new LocalDate(2013,1,1)));
        assertThat(LeaseTermFrequency.YEARLY.nextDate(new LocalDate(2012,4,15)), is(new LocalDate(2013,4,15)));
    }

    @Test
    public void whenNoFrequency() {
        assertThat(LeaseTermFrequency.NO_FREQUENCY.nextDate(new LocalDate(2012,1,1)), is(nullValue()));
        assertThat(LeaseTermFrequency.NO_FREQUENCY.nextDate(new LocalDate(2012,4,15)), is(nullValue()));
    }

}
