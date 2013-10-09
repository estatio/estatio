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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hamcrest.core.Is;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LocalDateIntervalTest {

    private LocalDateInterval periodInterval = LocalDateInterval.excluding(new LocalDate(2012, 1, 1), new LocalDate(2012, 4, 1));
    // exact match
    private LocalDateInterval interval1 = LocalDateInterval.including(new LocalDate(2012, 1, 1), new LocalDate(2012, 3, 31));
    // overlap
    private LocalDateInterval interval2 = LocalDateInterval.excluding(new LocalDate(2011, 11, 1), new LocalDate(2012, 5, 1));
    // ends in
    private LocalDateInterval interval3 = LocalDateInterval.excluding(new LocalDate(2011, 11, 1), new LocalDate(2012, 3, 1));
    // starts in
    private LocalDateInterval interval4 = LocalDateInterval.excluding(new LocalDate(2012, 2, 1), new LocalDate(2012, 5, 1));
    // start and ends in
    private LocalDateInterval interval5 = LocalDateInterval.excluding(new LocalDate(2012, 2, 1), new LocalDate(2012, 3, 1));
    // outside before
    private LocalDateInterval interval6 = LocalDateInterval.excluding(new LocalDate(2010, 1, 1), new LocalDate(2011, 1, 1));
    // outside after
    private LocalDateInterval interval7 = LocalDateInterval.excluding(new LocalDate(2013, 1, 1), new LocalDate(2014, 1, 1));
    // starts in, open ended
    private LocalDateInterval interval8 = LocalDateInterval.excluding(new LocalDate(2012, 2, 1), null);
    // open start, open end
    private LocalDateInterval interval9 = LocalDateInterval.excluding(null, null);

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
    public void startDate() {
        assertEquals(new LocalDate(2012, 1, 1), interval1.overlap(periodInterval).startDate());
        assertEquals(new LocalDate(2012, 1, 1), interval2.overlap(periodInterval).startDate());
        assertEquals(new LocalDate(2012, 1, 1), interval3.overlap(periodInterval).startDate());
        assertEquals(new LocalDate(2012, 2, 1), interval4.overlap(periodInterval).startDate());
        assertEquals(new LocalDate(2012, 2, 1), interval5.overlap(periodInterval).startDate());

        assertEquals(new LocalDate(2012, 2, 1), interval8.overlap(periodInterval).startDate());
        assertEquals(new LocalDate(2012, 1, 1), interval9.overlap(periodInterval).startDate());
    }

    @Test
    public void endDateExcluding() {
        assertEquals(new LocalDate(2012, 4, 1), interval1.overlap(periodInterval).endDateExcluding());
        assertEquals(new LocalDate(2012, 4, 1), interval2.overlap(periodInterval).endDateExcluding());
        assertEquals(new LocalDate(2012, 3, 1), interval3.overlap(periodInterval).endDateExcluding());
        assertEquals(new LocalDate(2012, 4, 1), interval4.overlap(periodInterval).endDateExcluding());
        assertEquals(new LocalDate(2012, 3, 1), interval5.overlap(periodInterval).endDateExcluding());
        assertEquals(null, interval6.overlap(periodInterval));
        assertEquals(null, interval7.overlap(periodInterval));
        assertEquals(new LocalDate(2012, 4, 1), interval8.overlap(periodInterval).endDateExcluding());
        assertEquals(new LocalDate(2012, 4, 1), interval9.overlap(periodInterval).endDateExcluding());
    }

    @Test
    public void days() {
        assertEquals(91, interval1.overlap(periodInterval).days());
        assertEquals(91, interval2.overlap(periodInterval).days());
        assertEquals(60, interval3.overlap(periodInterval).days());
        assertEquals(60, interval4.overlap(periodInterval).days());
        assertEquals(29, interval5.overlap(periodInterval).days());
        assertEquals(null, interval6.overlap(periodInterval));
        assertEquals(null, interval7.overlap(periodInterval));
        assertEquals(60, interval8.overlap(periodInterval).days());
        assertEquals(91, interval9.overlap(periodInterval).days());
    }

    @Test
    public void testContains() {
        assertTrue(periodInterval.contains(new LocalDate(2012, 1, 1)));
        assertTrue(periodInterval.contains(new LocalDate(2012, 3, 31)));
        // open interval
        assertTrue(interval8.contains(new LocalDate(2099, 1, 1)));
        assertFalse(interval8.contains(new LocalDate(2000, 1, 1)));

        assertTrue(interval9.contains(new LocalDate(2012, 3, 31)));
        assertFalse(periodInterval.contains(new LocalDate(2012, 4, 1)));

    }

    @Test
    public void testEndDateFromStartDate() {
        Assert.assertThat(interval1.endDateFromStartDate(), Is.is(interval1.startDate().minusDays(1)));
    }

    @Test
    public void testEmptyInterval() {
        LocalDateInterval myInterval = new LocalDateInterval(emptyInterval());
        Assert.assertNull(myInterval.startDate());
        Assert.assertNull(myInterval.endDate());
    }

    @Test
    public void testOverlap() {
        LocalDateInterval myInterval1 = LocalDateInterval.excluding(new LocalDate(2000, 1, 1), null);
        LocalDateInterval myInterval2 = LocalDateInterval.including(null, new LocalDate(2010, 1, 1));
        Assert.assertThat(myInterval1.overlap(myInterval2).startDate(), Is.is(new LocalDate(2000, 1, 1)));
        Assert.assertThat(myInterval1.overlap(myInterval2).endDate(), Is.is(new LocalDate(2010, 1, 1)));

        LocalDateInterval myInterval3 = LocalDateInterval.excluding(new LocalDate(2011, 1, 1), null);
        LocalDateInterval myInterval4 = LocalDateInterval.including(null, new LocalDate(2010, 1, 1));
        Assert.assertNull(myInterval3.overlap(myInterval4));
        Assert.assertNull(myInterval3.overlap(myInterval4));

    }

    private static Interval emptyInterval() {
        return null;
    }

}
