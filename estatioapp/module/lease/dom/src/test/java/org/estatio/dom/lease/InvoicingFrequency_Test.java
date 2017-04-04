package org.estatio.dom.lease;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Test;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.dom.invoice.InvoicingInterval;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoicingFrequency_Test {

    @Test
    public void testIntervalContaining() {
        testRange(InvoicingFrequency.MONTHLY_IN_ADVANCE, "2010-01-01", "2010-01-01/2010-02-01");
        testRange(InvoicingFrequency.QUARTERLY_IN_ADVANCE, "2010-01-01", "2010-01-01/2010-04-01");
        testRange(InvoicingFrequency.QUARTERLY_IN_ADVANCE_PLUS1M, "2009-11-01", "2009-11-01/2010-02-01");
        testRange(InvoicingFrequency.QUARTERLY_IN_ADVANCE_PLUS1M, "2010-02-01", "2010-02-01/2010-05-01");
        testRange(InvoicingFrequency.QUARTERLY_IN_ADVANCE_PLUS1M, "2010-05-01", "2010-05-01/2010-08-01");
        testRange(InvoicingFrequency.QUARTERLY_IN_ADVANCE_PLUS1M, "2010-08-01", "2010-08-01/2010-11-01");
        testRange(InvoicingFrequency.QUARTERLY_IN_ADVANCE_PLUS2M, "2009-12-01", "2009-12-01/2010-03-01");
        testRange(InvoicingFrequency.QUARTERLY_IN_ADVANCE_PLUS2M, "2010-03-01", "2010-03-01/2010-06-01");
        testRange(InvoicingFrequency.QUARTERLY_IN_ADVANCE_PLUS2M, "2010-06-01", "2010-06-01/2010-09-01");
        testRange(InvoicingFrequency.QUARTERLY_IN_ADVANCE_PLUS2M, "2010-09-01", "2010-09-01/2010-12-01");
        testRange(InvoicingFrequency.MONTHLY_IN_ADVANCE, "2010-01-01", "2010-01-01/2010-02-01");
        testRange(InvoicingFrequency.YEARLY_IN_ARREARS, "2010-01-01", "2010-01-01/2011-01-01");
        testRange(InvoicingFrequency.YEARLY_IN_ARREARS_PLUS6M, "2010-07-01", "2010-07-01/2011-07-01");
    }

    @Test
    public void testIntervalsInRange() {
        List<InvoicingInterval> intervalsInRange = InvoicingFrequency.QUARTERLY_IN_ADVANCE.intervalsInRange(new LocalDate(2012, 1, 1), new LocalDate(2014, 4, 1));
        assertThat(intervalsInRange).hasSize(9);
    }

    @Test
    public void testIntervalsInDueDateRange() {
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE, "2012-01-01/2014-04-01", 9);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE, "2013-12-31/2014-04-01", 1);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE, "2013-12-30/2013-12-31", 0);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE, "2013-12-29/2013-12-30", 0);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ARREARS, "2013-12-29/2013-12-30", 0);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE, "2014-01-02/2014-01-03", 0);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ARREARS, "2014-01-02/2014-01-03", 0);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ARREARS, "2013-12-29/2014-03-31", 1);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ARREARS, "2013-12-29/2014-04-01", 2);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE, "2013-12-29/2014-01-02", 1);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE, "2014-01-01/2014-01-01", 0);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE, "2014-01-01/2014-01-02", 1);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE, "2012-01-01/2014-01-01", 8);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE_PLUS2M, "2012-01-01/2012-03-01", 0);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE_PLUS2M, "2012-01-01/2012-03-02", 1);
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE_PLUS2M, "2012-03-01/2012-03-02", 1);
        dueDateRangeTester(InvoicingFrequency.FIXED_IN_ADVANCE, "2012-03-01/2012-03-02", 1);
        dueDateRangeTester(InvoicingFrequency.FIXED_IN_ARREARS, "2012-03-01/2012-03-02", 1);
        dueDateRangeTester(InvoicingFrequency.YEARLY_IN_ARREARS_PLUS6M, "2010-07-01/2011-06-30", 0);
        dueDateRangeTester(InvoicingFrequency.YEARLY_IN_ARREARS_PLUS6M, "2010-07-01/2011-07-01", 1);

    }

    @Test
    public void testIntervalsInDueDateRangeWithInterval() {
        // Frequency, period, source interval, expected interval, expected size
        dueDateRangeTester(InvoicingFrequency.QUARTERLY_IN_ADVANCE, "2012-01-01/2014-01-01", "2013-01-15/2014-01-16", "2012-01-01/2012-04-01:2012-01-01", 8);
        dueDateRangeTester(InvoicingFrequency.FIXED_IN_ARREARS, "2012-01-01/2014-01-01", "2012-01-01/2014-01-01", "2012-01-01/2014-01-01:2013-12-31", 1);
        dueDateRangeTester(InvoicingFrequency.FIXED_IN_ARREARS, "2012-01-01/2014-04-01", "2015-02-15/2015-06-16", null, 0);
        dueDateRangeTester(InvoicingFrequency.FIXED_IN_ARREARS, "2012-01-01/2014-04-01", "2011-01-31/2012-01-30", "2011-01-31/2012-01-30:2012-01-29", 1);
        dueDateRangeTester(InvoicingFrequency.FIXED_IN_ARREARS, "2012-01-01/2014-04-01", "2011-01-31/2011-12-31", null, 0);
        dueDateRangeTester(InvoicingFrequency.FIXED_IN_ADVANCE, "2012-01-01/2014-04-01", "2011-01-31/2011-12-31", null, 0);
    }

    @Test
    public void test_due_date() throws Exception {

        //Given
        final InvoicingInterval invoicingInterval = InvoicingFrequency.QUARTERLY_IN_ARREARS.intervalMatching(new LocalDate(2013, 1, 1));

        assertThat(invoicingInterval.dueDate()).isEqualTo(new LocalDate(2013,3,31));

    }

    private void dueDateRangeTester(
            final InvoicingFrequency frequency,
            final String intervalStr,
            final int result) {
        List<InvoicingInterval> intervalsInDueDateRange =
                frequency.intervalsInDueDateRange(
                        LocalDateInterval.parseString(intervalStr),
                        LocalDateInterval.parseString(intervalStr));
        assertThat(intervalsInDueDateRange).hasSize(result);
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
        assertThat(intervalsInDueDateRange).hasSize(expectedSize);
        assertThat(result).isEqualTo(expectedIntervalStr);
    }

    private void testRange(
            final InvoicingFrequency frequency,
            final String dateStr,
            final String expectedStr) {
        assertThat(frequency.intervalContaining(new LocalDate(dateStr)).asLocalDateInterval()).isEqualTo(LocalDateInterval.parseString(expectedStr));
    }

}
