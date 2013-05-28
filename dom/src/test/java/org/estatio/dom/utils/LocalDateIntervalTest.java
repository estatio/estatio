package org.estatio.dom.utils;

import static org.junit.Assert.*;

import org.estatio.dom.utils.LocalDateInterval.IntervalEnding;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

public class LocalDateIntervalTest {

    private LocalDateInterval periodInterval = new LocalDateInterval(new LocalDate(2012, 1, 1), new LocalDate(2012, 4, 1), IntervalEnding.EXCLUDING_END_DATE);
    // exact match
    private LocalDateInterval interval1 = new LocalDateInterval(new LocalDate(2012, 1, 1), new LocalDate(2012, 4, 1), IntervalEnding.EXCLUDING_END_DATE);
    // overlap
    private LocalDateInterval interval2 = new LocalDateInterval(new LocalDate(2011, 11, 1), new LocalDate(2012, 5, 1), IntervalEnding.EXCLUDING_END_DATE);
    // ends in
    private LocalDateInterval interval3 = new LocalDateInterval(new LocalDate(2011, 11, 1), new LocalDate(2012, 3, 1), IntervalEnding.EXCLUDING_END_DATE);
    // starts in
    private LocalDateInterval interval4 = new LocalDateInterval(new LocalDate(2012, 2, 1), new LocalDate(2012, 5, 1), IntervalEnding.EXCLUDING_END_DATE);
    // start and ends in
    private LocalDateInterval interval5 = new LocalDateInterval(new LocalDate(2012, 2, 1), new LocalDate(2012, 3, 1), IntervalEnding.EXCLUDING_END_DATE);
    // outside before
    private LocalDateInterval interval6 = new LocalDateInterval(new LocalDate(2010, 1, 1), new LocalDate(2011, 1, 1), IntervalEnding.EXCLUDING_END_DATE);
    // outside after
    private LocalDateInterval interval7 = new LocalDateInterval(new LocalDate(2013, 1, 1), new LocalDate(2014, 1, 1), IntervalEnding.EXCLUDING_END_DATE);
    // starts in, open ended
    private LocalDateInterval interval8 = new LocalDateInterval(new LocalDate(2012, 2, 1), null, IntervalEnding.EXCLUDING_END_DATE);
    // open start, open end
    private LocalDateInterval interval9 = new LocalDateInterval(null, null, IntervalEnding.EXCLUDING_END_DATE);

    @Before
    public void setup() {

    }

    @Test
    public void testIsWithinParent() {
        assertTrue(interval1.overlaps(periodInterval));
        assertTrue(interval2.overlaps(periodInterval));
        assertTrue(interval3.overlaps(periodInterval));
        assertTrue(interval4.overlaps(periodInterval));
        assertTrue(interval5.overlaps(periodInterval));
        assertFalse(interval6.overlaps(periodInterval));
        assertFalse(interval7.overlaps(periodInterval));
        assertTrue(interval8.overlaps(periodInterval));
        assertTrue(interval9.overlaps(periodInterval));
    }

    @Test
    public void testIsFullInterval() {
        assertTrue(interval1.contains(periodInterval));
        assertTrue(interval2.contains(periodInterval));
        assertFalse(interval3.contains(periodInterval));
        assertFalse(interval4.contains(periodInterval));
        assertFalse(interval5.contains(periodInterval));
        assertFalse(interval6.contains(periodInterval));
        assertFalse(interval7.contains(periodInterval));
        assertFalse(interval8.contains(periodInterval));
        assertTrue(interval9.contains(periodInterval));
    }

    @Test
    public void testGetActualStartDate() {
        assertEquals(new LocalDate(2012, 1, 1), interval1.overlap(periodInterval).getStartDate());
        assertEquals(new LocalDate(2012, 1, 1), interval2.overlap(periodInterval).getStartDate());
        assertEquals(new LocalDate(2012, 1, 1), interval3.overlap(periodInterval).getStartDate());
        assertEquals(new LocalDate(2012, 2, 1), interval4.overlap(periodInterval).getStartDate());
        assertEquals(new LocalDate(2012, 2, 1), interval5.overlap(periodInterval).getStartDate());
        assertEquals(null, interval6.overlap(periodInterval));
        assertEquals(null, interval7.overlap(periodInterval));
        assertEquals(new LocalDate(2012, 2, 1), interval8.overlap(periodInterval).getStartDate());
        assertEquals(new LocalDate(2012, 1, 1), interval9.overlap(periodInterval).getStartDate());
    }

    @Test
    public void testGetActualEndDate() {
        assertEquals(new LocalDate(2012, 4, 1), interval1.overlap(periodInterval).getEndDate());
        assertEquals(new LocalDate(2012, 4, 1), interval2.overlap(periodInterval).getEndDate());
        assertEquals(new LocalDate(2012, 3, 1), interval3.overlap(periodInterval).getEndDate());
        assertEquals(new LocalDate(2012, 4, 1), interval4.overlap(periodInterval).getEndDate());
        assertEquals(new LocalDate(2012, 3, 1), interval5.overlap(periodInterval).getEndDate());
        assertEquals(null, interval6.overlap(periodInterval));
        assertEquals(null, interval7.overlap(periodInterval));
        assertEquals(new LocalDate(2012, 4, 1), interval8.overlap(periodInterval).getEndDate());
        assertEquals(new LocalDate(2012, 4, 1), interval9.overlap(periodInterval).getEndDate());
    }

    @Test
    public void testGetDays() {
        assertEquals(91, interval1.overlap(periodInterval).getDays());
        assertEquals(91, interval2.overlap(periodInterval).getDays());
        assertEquals(60, interval3.overlap(periodInterval).getDays());
        assertEquals(60, interval4.overlap(periodInterval).getDays());
        assertEquals(29, interval5.overlap(periodInterval).getDays());

        assertEquals(60, interval8.overlap(periodInterval).getDays());
        assertEquals(91, interval9.overlap(periodInterval).getDays());
    }

    @Test
    public void testContains() {
        assertTrue(periodInterval.contains(new LocalDate(2012,1,1)));
        assertTrue(periodInterval.contains(new LocalDate(2012,3,31)));
        assertFalse(periodInterval.contains(new LocalDate(2012,4,1)));
    }

    
}
