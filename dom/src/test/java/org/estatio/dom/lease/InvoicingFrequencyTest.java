package org.estatio.dom.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Test;

import org.estatio.dom.invoice.InvoicingInterval;

public class InvoicingFrequencyTest {

    @Test
    public void intervalContaining() {
        assertThat(InvoicingFrequency.MONTHLY_IN_ADVANCE.intervalContaining(new LocalDate(2010,1,1)).toString(), is("2010-01-01/2010-02-01"));
        assertThat(InvoicingFrequency.QUARTERLY_IN_ADVANCE.intervalContaining(new LocalDate(2010,1,1)).toString(), is("2010-01-01/2010-04-01"));
        assertThat(InvoicingFrequency.MONTHLY_IN_ADVANCE.intervalContaining(new LocalDate(2010,1,1)).dueDate(), is(new LocalDate("2010-01-01")));
        assertThat(InvoicingFrequency.YEARLY_IN_ARREARS.intervalContaining(new LocalDate(2010,1,1)).dueDate(), is(new LocalDate("2010-12-31")));
    }

    @Test
    public void intervalsInRange() {
        List<InvoicingInterval> intervalsInRange = InvoicingFrequency.QUARTERLY_IN_ADVANCE.intervalsInRange(new LocalDate(2012,1,1), new LocalDate(2014,4,1));
        assertThat(intervalsInRange.size(), is(9));
    }
    @Test
    public void intervalsInDueDateRange() {
        dueDateRangeTester("2012-01-01", "2014-04-01", 9);
        dueDateRangeTester("2013-12-31", "2014-04-01", 2);
        dueDateRangeTester("2013-12-30", "2013-12-31", 1);
        dueDateRangeTester("2014-01-01", "2014-01-01", 0);
    }

    public void dueDateRangeTester(String start, String end, int result) {
        List<InvoicingInterval> intervalsInDueDateRange = InvoicingFrequency.QUARTERLY_IN_ADVANCE.intervalsInDueDateRange(new LocalDate(start), new LocalDate(end));
        assertThat(intervalsInDueDateRange.size(), is(result));
    }

}
