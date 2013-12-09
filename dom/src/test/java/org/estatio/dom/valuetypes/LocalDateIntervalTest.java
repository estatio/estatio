/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.valuetypes;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.valuetypes.LocalDateInterval.IntervalEnding;

public class LocalDateIntervalTest {

    private LocalDateInterval period120101to120401
    = LocalDateInterval.excluding(new LocalDate(2012, 1, 1), new LocalDate(2012, 4, 1));
    private LocalDateInterval interval120101to120331incl = LocalDateInterval.including(new LocalDate(2012, 1, 1), new LocalDate(2012, 3, 31));
    private LocalDateInterval interval111101to120501 = LocalDateInterval.excluding(new LocalDate(2011, 11, 1), new LocalDate(2012, 5, 1));
    private LocalDateInterval interval111101to120301 = LocalDateInterval.excluding(new LocalDate(2011, 11, 1), new LocalDate(2012, 3, 1));
    private LocalDateInterval interval120201to120501 = LocalDateInterval.excluding(new LocalDate(2012, 2, 1), new LocalDate(2012, 5, 1));
    private LocalDateInterval interval120201to120301 = LocalDateInterval.excluding(new LocalDate(2012, 2, 1), new LocalDate(2012, 3, 1));
    private LocalDateInterval interval100101to110101 = LocalDateInterval.excluding(new LocalDate(2010, 1, 1), new LocalDate(2011, 1, 1));
    private LocalDateInterval interval130101to140101 = LocalDateInterval.excluding(new LocalDate(2013, 1, 1), new LocalDate(2014, 1, 1));
    private LocalDateInterval interval120201toOpen = LocalDateInterval.excluding(new LocalDate(2012, 2, 1), null);
    private LocalDateInterval interval120101toOpen = LocalDateInterval.excluding(new LocalDate(2010, 2, 1), null);
    private LocalDateInterval intervalOpen = LocalDateInterval.excluding(null, null);

    @Before
    public void setup() {

    }

    @Test
    public void test() {
        LocalDate startDate = new LocalDate(2012, 1, 1);
        LocalDate endDate = new LocalDate(2012, 1, 31);
        Interval jodaInterval = new Interval(startDate.toInterval().getStartMillis(), endDate.toInterval().getEndMillis());
        LocalDateInterval interval = new LocalDateInterval(jodaInterval);
        assertThat(interval.startDate(), is(startDate));
        assertThat(interval.endDate(), is(endDate));
    }
    
    @Test
    public void testInvalid() {
        assertFalse(new LocalDateInterval(new LocalDate(2011,1,1), new LocalDate(2010,1,1)).isValid());
        assertFalse(new LocalDateInterval(new LocalDate(2011,1,1), new LocalDate(2010,12,30)).isValid());
        assertTrue(new LocalDateInterval(new LocalDate(2011,1,1), new LocalDate(2010,12,31)).isValid());
    }

    @Test
    public void testIsWithinParent() {
        assertTrue(interval120101to120331incl.overlaps(period120101to120401));
        assertTrue(interval111101to120501.overlaps(period120101to120401));
        assertTrue(interval111101to120301.overlaps(period120101to120401));
        assertTrue(interval120201to120501.overlaps(period120101to120401));
        assertTrue(interval120201to120301.overlaps(period120101to120401));
        assertFalse(interval100101to110101.overlaps(period120101to120401));
        assertFalse(interval130101to140101.overlaps(period120101to120401));
        assertTrue(interval120201toOpen.overlaps(period120101to120401));
        assertTrue(intervalOpen.overlaps(period120101to120401));
    }

    @Test
    public void testIsFullInterval() {
        assertTrue(interval120101to120331incl.contains(period120101to120401));
        assertTrue(interval111101to120501.contains(period120101to120401));
        assertFalse(interval111101to120301.contains(period120101to120401));
        assertFalse(interval120201to120501.contains(period120101to120401));
        assertFalse(interval120201to120301.contains(period120101to120401));
        assertFalse(interval100101to110101.contains(period120101to120401));
        assertFalse(interval130101to140101.contains(period120101to120401));
        assertFalse(interval120201toOpen.contains(period120101to120401));
        assertTrue(intervalOpen.contains(period120101to120401));
    }

    @Test
    public void startDate() {
        assertEquals(new LocalDate(2012, 1, 1), interval120101to120331incl.overlap(period120101to120401).startDate());
        assertEquals(new LocalDate(2012, 1, 1), interval111101to120501.overlap(period120101to120401).startDate());
        assertEquals(new LocalDate(2012, 1, 1), interval111101to120301.overlap(period120101to120401).startDate());
        assertEquals(new LocalDate(2012, 2, 1), interval120201to120501.overlap(period120101to120401).startDate());
        assertEquals(new LocalDate(2012, 2, 1), interval120201to120301.overlap(period120101to120401).startDate());
        assertEquals(new LocalDate(2012, 2, 1), interval120201toOpen.overlap(period120101to120401).startDate());
        assertEquals(new LocalDate(2012, 1, 1), intervalOpen.overlap(period120101to120401).startDate());
    }

    @Test
    public void endDateExcluding() {
        assertEquals(new LocalDate(2012, 4, 1), interval120101to120331incl.overlap(period120101to120401).endDateExcluding());
        assertEquals(new LocalDate(2012, 4, 1), interval111101to120501.overlap(period120101to120401).endDateExcluding());
        assertEquals(new LocalDate(2012, 3, 1), interval111101to120301.overlap(period120101to120401).endDateExcluding());
        assertEquals(new LocalDate(2012, 4, 1), interval120201to120501.overlap(period120101to120401).endDateExcluding());
        assertEquals(new LocalDate(2012, 3, 1), interval120201to120301.overlap(period120101to120401).endDateExcluding());
        assertEquals(new LocalDate(2012, 4, 1), interval120201toOpen.overlap(period120101to120401).endDateExcluding());
        assertEquals(new LocalDate(2012, 4, 1), intervalOpen.overlap(period120101to120401).endDateExcluding());
    }

    @Test
    public void days() {
        assertEquals(91, interval120101to120331incl.overlap(period120101to120401).days());
        assertEquals(91, interval111101to120501.overlap(period120101to120401).days());
        assertEquals(60, interval111101to120301.overlap(period120101to120401).days());
        assertEquals(60, interval120201to120501.overlap(period120101to120401).days());
        assertEquals(29, interval120201to120301.overlap(period120101to120401).days());
        assertEquals(60, interval120201toOpen.overlap(period120101to120401).days());
        assertEquals(91, intervalOpen.overlap(period120101to120401).days());
        assertEquals(91, period120101to120401.overlap(intervalOpen).days());
        assertEquals(0, intervalOpen.overlap(intervalOpen).days());
    }

    @Test
    public void testContains() {
        assertTrue(period120101to120401.contains(new LocalDate(2012, 1, 1)));
        assertTrue(period120101to120401.contains(new LocalDate(2012, 3, 31)));
        assertTrue(interval120201toOpen.contains(new LocalDate(2099, 1, 1)));
        assertFalse(interval120201toOpen.contains(new LocalDate(2000, 1, 1)));
        assertTrue(interval120201toOpen.contains(new LocalDate(2012, 3, 1)));
        assertTrue(intervalOpen.contains(new LocalDate(2012, 3, 31)));
        assertFalse(period120101to120401.contains(new LocalDate(2012, 4, 1)));
    }

    @Test
    public void testEndDateFromStartDate() {
        assertThat(interval120101to120331incl.endDateFromStartDate(), is(interval120101to120331incl.startDate().minusDays(1)));
    }

    @Test
    public void testEmptyInterval() {
        LocalDateInterval myInterval = new LocalDateInterval();
        assertNull(myInterval.startDate());
        assertNull(myInterval.endDate());
    }

    @Test
    public void testOverlap() {
        LocalDateInterval interval1 = LocalDateInterval.excluding(new LocalDate(2000, 1, 1), null);
        LocalDateInterval interval2 = LocalDateInterval.including(null, new LocalDate(2010, 1, 1));
        assertThat(interval1.overlap(interval2).startDate(), is(new LocalDate(2000, 1, 1)));
        assertThat(interval1.overlap(interval2).endDate(), is(new LocalDate(2010, 1, 1)));

        LocalDateInterval myInterval3 = LocalDateInterval.excluding(new LocalDate(2011, 1, 1), null);
        LocalDateInterval myInterval4 = LocalDateInterval.including(null, new LocalDate(2010, 1, 1));
        assertNull(myInterval3.overlap(myInterval4));
        
        assertThat(interval120101toOpen.overlap(interval120201toOpen), is(new LocalDateInterval(new LocalDate(2012,2,1), null)));
        
    }

    @Test
    public void testOpen() {
        assertThat(parseLocalDateInterval("*/*").overlap(parseLocalDateInterval("*/*")), is(parseLocalDateInterval("*/*")));
    }
    
    @Test
    public void testString() {
        assertThat(interval120201toOpen.toString(), is("2012-02-01/----------"));
    }

    @Test
    public void testEquals(){
        assertTrue(new LocalDateInterval().equals(new LocalDateInterval()));
        assertTrue(new LocalDateInterval(null, null, IntervalEnding.EXCLUDING_END_DATE).equals(new LocalDateInterval(null, null, IntervalEnding.INCLUDING_END_DATE)));
    }
    
    private LocalDateInterval parseLocalDateInterval(String input){
        String[] values = input.split("/");
        return new LocalDateInterval(parseLocalDate(values[0]), parseLocalDate(values[1]), IntervalEnding.INCLUDING_END_DATE);
    }

    static private LocalDate parseLocalDate(String input) {
        if (input.contains("--") || input.contains("*"))
            return null;
        return LocalDate.parse(input);

    }
}
