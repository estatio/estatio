package org.estatio.dom.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;
import org.junit.Test;

public class InvoicingFrequencyTest {

    @Test
    public void intervalContaining() {
        assertThat(InvoicingFrequency.MONTHLY_IN_ADVANCE.intervalContaining(new LocalDate(2010,1,1)).toString(), is("2010-01-01/2010-02-01"));
        assertThat(InvoicingFrequency.QUARTERLY_IN_ADVANCE.intervalContaining(new LocalDate(2010,1,1)).toString(), is("2010-01-01/2010-04-01"));
        assertThat(InvoicingFrequency.MONTHLY_IN_ADVANCE.intervalContaining(new LocalDate(2010,1,1)).dueDate(), is(new LocalDate("2010-01-01")));
        assertThat(InvoicingFrequency.YEARLY_IN_ARREARS.intervalContaining(new LocalDate(2010,1,1)).dueDate(), is(new LocalDate("2011-01-01")));
    }
}
