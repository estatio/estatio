package org.estatio.dom.utils;

import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Test;

public class DateTimeUtilsTest {

    @Test
    public void test() {
        Period period = DateTimeUtils.stringToPeriod("6y6m");
        LocalDate startDate = new LocalDate(2000, 1, 1);
        Assert.assertThat(startDate.plus(period), Is.is(new LocalDate(2006, 7, 1)));
    }

    @Test
    public void testWithSpaces() {
        Period period = DateTimeUtils.stringToPeriod("  6Y  6m  ");
        LocalDate startDate = new LocalDate(2000, 1, 1);
        Assert.assertThat(startDate.plus(period), Is.is(new LocalDate(2006, 7, 1)));
    }

    @Test
    public void testMalformed() {
        Period period = DateTimeUtils.stringToPeriod("6x6y");
        LocalDate startDate = new LocalDate(2000, 1, 1);
        Assert.assertThat(startDate.plus(period), Is.is(new LocalDate(2000, 1, 1)));
    }

    @Test
    public void testPeriodtoString() throws Exception {
        Period period = new Period(new LocalDate(2000, 1, 1), new LocalDate(2006, 7, 2));
        Assert.assertThat(DateTimeUtils.periodToString(period), Is.is("6 year(s) 6 month(s) 1 day(s)"));
    }

}
