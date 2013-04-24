package org.estatio.dom.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

public class DateRangeTest {
    private DateRange boundingRange = new DateRange(new LocalDate(2012, 1, 1), new LocalDate(2012, 4, 1));
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
    // open start, open end
    private DateRange dateRange9 = new DateRange(null, null);

    @Before
    public void setup() {
        dateRange1.setBoundingRange(boundingRange);
        dateRange2.setBoundingRange(boundingRange);
        dateRange3.setBoundingRange(boundingRange);
        dateRange4.setBoundingRange(boundingRange);
        dateRange5.setBoundingRange(boundingRange);
        dateRange6.setBoundingRange(boundingRange);
        dateRange7.setBoundingRange(boundingRange);
        dateRange8.setBoundingRange(boundingRange);
        dateRange9.setBoundingRange(boundingRange);
    }

    @Test
    public void testIsWithinParent() {
        assertTrue(dateRange1.isWithinParent());
        assertTrue(dateRange2.isWithinParent());
        assertTrue(dateRange3.isWithinParent());
        assertTrue(dateRange4.isWithinParent());
        assertTrue(dateRange5.isWithinParent());
        assertFalse(dateRange6.isWithinParent());
        assertFalse(dateRange7.isWithinParent());
        assertTrue(dateRange8.isWithinParent());
        assertTrue(dateRange9.isWithinParent());
    }

    @Test
    public void testIsFullInterval() {
        assertTrue(dateRange1.isFullInterval());
        assertTrue(dateRange2.isFullInterval());
        assertFalse(dateRange3.isFullInterval());
        assertFalse(dateRange4.isFullInterval());
        assertFalse(dateRange5.isFullInterval());
        assertFalse(dateRange6.isFullInterval());
        assertFalse(dateRange7.isFullInterval());
        assertFalse(dateRange8.isFullInterval());
        assertTrue(dateRange9.isFullInterval());
    }

    @Test
    public void testGetActualStartDate() {
        assertEquals(new LocalDate(2012, 1, 1), dateRange1.getActualStartDate());
        assertEquals(new LocalDate(2012, 1, 1), dateRange2.getActualStartDate());
        assertEquals(new LocalDate(2012, 1, 1), dateRange3.getActualStartDate());
        assertEquals(new LocalDate(2012, 2, 1), dateRange4.getActualStartDate());
        assertEquals(new LocalDate(2012, 2, 1), dateRange5.getActualStartDate());
        assertEquals(null, dateRange6.getActualStartDate());
        assertEquals(null, dateRange7.getActualStartDate());
        assertEquals(new LocalDate(2012, 2, 1), dateRange8.getActualStartDate());
        assertEquals(new LocalDate(2012, 1, 1), dateRange9.getActualStartDate());
    }

    @Test
    public void testGetActualEndDate() {
        assertEquals(new LocalDate(2012, 4, 1), dateRange1.getActualEndDate());
        assertEquals(new LocalDate(2012, 4, 1), dateRange2.getActualEndDate());
        assertEquals(new LocalDate(2012, 3, 1), dateRange3.getActualEndDate());
        assertEquals(new LocalDate(2012, 4, 1), dateRange4.getActualEndDate());
        assertEquals(new LocalDate(2012, 3, 1), dateRange5.getActualEndDate());
        assertEquals(null, dateRange6.getActualEndDate());
        assertEquals(null, dateRange7.getActualEndDate());
        assertEquals(new LocalDate(2012, 4, 1), dateRange8.getActualEndDate());
        assertEquals(new LocalDate(2012, 4, 1), dateRange9.getActualEndDate());
    }

    @Test
    public void testGetActualDays() {
        assertEquals(91, dateRange1.getActualDays());
        assertEquals(91, dateRange2.getActualDays());
        assertEquals(60, dateRange3.getActualDays());
        assertEquals(60, dateRange4.getActualDays());
        assertEquals(29, dateRange5.getActualDays());
        assertEquals(0, dateRange6.getActualDays());
        assertEquals(0, dateRange7.getActualDays());
        assertEquals(60, dateRange8.getActualDays());
        assertEquals(91, dateRange9.getActualDays());
    }

    @Test
    public void testGetDays() {
        assertEquals(91, boundingRange.getDays());
        assertEquals(360464, dateRange8.getDays());

    }

}
