package org.estatio.dom.utils;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DateRangeTest {
    private DateRange parentRange = new DateRange(new LocalDate(2012, 1, 1), new LocalDate(2012, 4, 1));
    // exact match
    private DateRange dateRange1 = new DateRange(new LocalDate(2012, 1, 1), new LocalDate(2012, 4, 1));
    // overlap
    private DateRange dateRange2 = new DateRange(new LocalDate(2011, 11, 1), new LocalDate(2012, 5, 1));
    // ends in
    private DateRange dateRange3 = new DateRange(new LocalDate(2011, 11, 1), new LocalDate(2012, 3, 1));
    // starts in
    private DateRange dateRange4 = new DateRange(new LocalDate(2012, 2, 1), new LocalDate(2012, 5, 1));
    // start and ends in
    private DateRange dateRange5 = new DateRange(new LocalDate(2012, 2, 1), new LocalDate(2012, 3, 1));
    // outside before
    private DateRange dateRange6 = new DateRange(new LocalDate(2010, 1, 1), new LocalDate(2011, 1, 1));
    // outside after
    private DateRange dateRange7 = new DateRange(new LocalDate(2013, 1, 1), new LocalDate(2014, 1, 1));
    // starts in, open ended
    private DateRange dateRange8 = new DateRange(new LocalDate(2012, 2, 1), null);

    @Before
    public void setup() {
        dateRange1.setParentRange(parentRange);
        dateRange2.setParentRange(parentRange);
        dateRange3.setParentRange(parentRange);
        dateRange4.setParentRange(parentRange);
        dateRange5.setParentRange(parentRange);
        dateRange6.setParentRange(parentRange);
        dateRange7.setParentRange(parentRange);
        dateRange8.setParentRange(parentRange);
    }

    @Test
    public void testIsWithinParent() {
        Assert.assertTrue(dateRange1.isWithinParent());
        Assert.assertTrue(dateRange2.isWithinParent());
        Assert.assertTrue(dateRange3.isWithinParent());
        Assert.assertTrue(dateRange4.isWithinParent());
        Assert.assertTrue(dateRange5.isWithinParent());
        Assert.assertFalse(dateRange6.isWithinParent());
        Assert.assertFalse(dateRange7.isWithinParent());
        Assert.assertTrue(dateRange8.isWithinParent());
    }

    @Test
    public void testIsFullInterval() {
        Assert.assertTrue(dateRange1.isFullInterval());
        Assert.assertTrue(dateRange2.isFullInterval());
        Assert.assertFalse(dateRange3.isFullInterval());
        Assert.assertFalse(dateRange4.isFullInterval());
        Assert.assertFalse(dateRange5.isFullInterval());
        Assert.assertFalse(dateRange6.isFullInterval());
        Assert.assertFalse(dateRange7.isFullInterval());
        Assert.assertFalse(dateRange8.isFullInterval());
    }

    @Test
    public void testGetActualStartDate() {
        Assert.assertEquals(new LocalDate(2012, 1, 1), dateRange1.getActualStartDate());
        Assert.assertEquals(new LocalDate(2012, 1, 1), dateRange2.getActualStartDate());
        Assert.assertEquals(new LocalDate(2012, 1, 1), dateRange3.getActualStartDate());
        Assert.assertEquals(new LocalDate(2012, 2, 1), dateRange4.getActualStartDate());
        Assert.assertEquals(new LocalDate(2012, 2, 1), dateRange5.getActualStartDate());
        Assert.assertEquals(null, dateRange6.getActualStartDate());
        Assert.assertEquals(null, dateRange7.getActualStartDate());
        Assert.assertEquals(new LocalDate(2012, 2, 1), dateRange8.getActualStartDate());
    }

    @Test
    public void testGetActualEndDate() {
        Assert.assertEquals(new LocalDate(2012, 4, 1), dateRange1.getActualEndDate());
        Assert.assertEquals(new LocalDate(2012, 4, 1), dateRange2.getActualEndDate());
        Assert.assertEquals(new LocalDate(2012, 3, 1), dateRange3.getActualEndDate());
        Assert.assertEquals(new LocalDate(2012, 4, 1), dateRange4.getActualEndDate());
        Assert.assertEquals(new LocalDate(2012, 3, 1), dateRange5.getActualEndDate());
        Assert.assertEquals(null, dateRange6.getActualEndDate());
        Assert.assertEquals(null, dateRange7.getActualEndDate());
        Assert.assertEquals(new LocalDate(2012, 4, 1), dateRange8.getActualEndDate());
    }

    @Test
    public void testGetActualDays() {
        Assert.assertEquals(91, dateRange1.getActualDays());
        Assert.assertEquals(91, dateRange2.getActualDays());
        Assert.assertEquals(60, dateRange3.getActualDays());
        Assert.assertEquals(60, dateRange4.getActualDays());
        Assert.assertEquals(29, dateRange5.getActualDays());
        Assert.assertEquals(0, dateRange6.getActualDays());
        Assert.assertEquals(0, dateRange7.getActualDays());
        Assert.assertEquals(60, dateRange8.getActualDays());
    }

    @Test
    public void testGetDays() {
        Assert.assertEquals(91, parentRange.getDays());
        Assert.assertEquals(360464, dateRange8.getDays());

    }

}
