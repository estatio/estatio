package org.estatio.capex.dom.time;

import org.joda.time.LocalDate;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeInterval_Test {

    @Test
    public void different_startDates() throws Exception {

        final TimeInterval firstOne = new TimeInterval(null, new LocalDate(2017, 6, 5), null, null, null);
        final TimeInterval secondOne = new TimeInterval(null, new LocalDate(2017, 6, 4), null, null, null);

        final int rsult = firstOne.compareTo(secondOne);

        assertThat(rsult).isEqualTo(-1);
    }

    @Test
    public void same_startDates_different_name() throws Exception {

        final TimeInterval firstOne = new TimeInterval("A", new LocalDate(2017, 6, 4), null, null, null);
        final TimeInterval secondOne = new TimeInterval("B", new LocalDate(2017, 6, 4), null, null, null);

        final int rsult = firstOne.compareTo(secondOne);

        assertThat(rsult).isEqualTo(-1);
    }

    @Test
    public void same_startDates_and_name() throws Exception {

        final TimeInterval firstOne = new TimeInterval("A", new LocalDate(2017, 6, 4), null, null, null);
        final TimeInterval secondOne = new TimeInterval("A", new LocalDate(2017, 6, 4), null, null, null);

        final int rsult = firstOne.compareTo(secondOne);

        assertThat(rsult).isEqualTo(0);

    }

}