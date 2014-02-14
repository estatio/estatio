package org.estatio.dom.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Test;

import org.estatio.dom.invoice.InvoicingInterval;
import org.estatio.dom.valuetypes.LocalDateInterval;

public class InvoicingFrequencyTest {

    @Test
    public void testIntervalContaining() {
        testRange(InvoicingFrequency.MONTHLY_IN_ADVANCE, "2010-01-01", "2010-01-01/2010-02-01");
        testRange(InvoicingFrequency.QUARTERLY_IN_ADVANCE, "2010-01-01", "2010-01-01/2010-04-01");
        testRange(InvoicingFrequency.MONTHLY_IN_ADVANCE, "2010-01-01", "2010-01-01/2010-02-01");
        testRange(InvoicingFrequency.YEARLY_IN_ARREARS, "2010-01-01", "2010-01-01/2011-01-01");
    }

    @Test
    public void testIntervalsInRange() {
        List<InvoicingInterval> intervalsInRange = InvoicingFrequency.QUARTERLY_IN_ADVANCE.intervalsInRange(new LocalDate(2012, 1, 1), new LocalDate(2014, 4, 1));
        assertThat(intervalsInRange.size(), is(9));
    }

    @Test
    public void testIntervalsInDueDateRange() {
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE, "2012-01-01/2014-04-01", 9);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE, "2013-12-31/2014-04-01", 2);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE, "2013-12-30/2013-12-31", 1);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE, "2014-01-01/2014-01-01", 0);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE, "2012-01-01/2014-01-01", 8);
    }

    @Test
    public void testIntervalsInDueDateRangeWithInterval() {
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE, "2012-01-01/2014-01-01", "2013-01-15/2014-01-16", "2012-01-01/2012-04-01:2012-01-01", 8);
        dueDateRangeTester(InvoicingFrequency.FIXED_IN_ARREARS, "2012-01-01/2014-01-01", "2012-01-01/2014-01-01", "2012-01-01/2014-01-01:2013-12-31", 1);
        dueDateRangeTester(InvoicingFrequency.FIXED_IN_ARREARS, "2012-01-01/2014-04-01", "2015-02-15/2015-06-16", null, 0);
        dueDateRangeTester(InvoicingFrequency.FIXED_IN_ARREARS, "2012-01-01/2014-04-01", "2011-01-31/2012-01-30", "2011-01-31/2012-01-30:2012-01-29", 1);
        dueDateRangeTester(InvoicingFrequency.FIXED_IN_ARREARS, "2012-01-01/2014-04-01", "2011-01-31/2011-12-31", null, 0);
    }

    private void dueDateRangeTester(
            final InvoicingFrequency frequency,
            final String intervalStr,
            final int result) {
        List<InvoicingInterval> intervalsInDueDateRange =
                frequency.intervalsInDueDateRange(
                        LocalDateInterval.parseString(intervalStr),
                        LocalDateInterval.parseString(intervalStr));
        assertThat(intervalsInDueDateRange.size(), is(result));
    }

    private void dueDateRangeTester(
            final InvoicingFrequency frequency,
            final String periodIntervalStr,
            final String sourceIntervalStr,
            final String expectedIntervalStr,
            final int expectedSize) {
        List<InvoicingInterval> intervalsInDueDateRange =
                frequency.intervalsInDueDateRange(
                        LocalDateInterval.parseString(periodIntervalStr),
                        LocalDateInterval.parseString(sourceIntervalStr));
        String result = intervalsInDueDateRange.size() == 0 ? null : intervalsInDueDateRange.get(0).toString();
        assertThat(intervalsInDueDateRange.size(), is(expectedSize));
        assertThat(result, is(expectedIntervalStr));
    }

    private void testRange(
            final InvoicingFrequency frequency,
            final String dateStr,
            final String expectedStr) {
        assertThat(frequency.intervalContaining(new LocalDate(dateStr)).asLocalDateInterval(),
                is(LocalDateInterval.parseString(expectedStr)));
    }

}
