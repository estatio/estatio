/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.dom.utils;

import java.util.List;
import org.hamcrest.core.Is;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class CalendarUtilsTest {

    public static class IntervalContaining extends CalendarUtilsTest {

        @Test
        public void intervalContainingTest() throws Exception {
            assertEquals(CalendarUtils.intervalContaining(new LocalDate(2012, 2, 1), "RRULE:FREQ=MONTHLY;INTERVAL=3"), new Interval(new LocalDate(2012, 1, 1).toInterval().getStartMillis(), new LocalDate(2012, 4, 1).toInterval().getStartMillis()));
        }

    }

    public static class IntervalMatching extends CalendarUtilsTest {

        @Test
        public void intervalMatchingTest() throws Exception {
            assertEquals(CalendarUtils.intervalMatching(new LocalDate(2012, 1, 1), "RRULE:FREQ=MONTHLY;INTERVAL=3"), new Interval(new LocalDate(2012, 1, 1).toInterval().getStartMillis(), new LocalDate(2012, 4, 1).toInterval().getStartMillis()));
            assertEquals(CalendarUtils.intervalMatching(new LocalDate(2012, 1, 1), "RRULE:FREQ=YEARLY;INTERVAL=1"), new Interval(new LocalDate(2012, 1, 1).toInterval().getStartMillis(), new LocalDate(2013, 1, 1).toInterval().getStartMillis()));
        }

        @Test
        public void intervalMatchingNotExactDateTest() throws Exception {
            assertNull(CalendarUtils.intervalMatching(new LocalDate(2012, 1, 2), "RRULE:FREQ=MONTHLY;INTERVAL=3"));
        }

    }

    public static class CurrentInterval extends CalendarUtilsTest {

        @Test
        public void zaraIntervalTest() throws Exception {
            assertThat(CalendarUtils.currentInterval(new LocalDate(2012, 3, 1), "RRULE:FREQ=MONTHLY;INTERVAL=3", new LocalDate(2010, 2, 1)), Is.is(LocalDateInterval.excluding(new LocalDate(2012, 2, 1), new LocalDate(2012, 5, 1)).asInterval()));
        }

        @Test
        public void zaraIntervalTest2() throws Exception {
            assertEquals(CalendarUtils.currentInterval(new LocalDate(2012, 3, 1), "RRULE:FREQ=MONTHLY;INTERVAL=3;BYMONTH=2,5,8,11", new LocalDate(2010, 1, 1)), new Interval(new LocalDate(2012, 2, 1).toInterval().getStartMillis(), new LocalDate(2012, 5, 1).toInterval().getStartMillis()));
        }

    }

    public static class IntervalsInRange extends CalendarUtilsTest {

        @Test
        public void intervalsInRangeTest() {
            List<Interval> intervals = CalendarUtils.intervalsInRange(new LocalDate(2012, 2, 1), new LocalDate(2012, 8, 1), "RRULE:FREQ=MONTHLY;INTERVAL=3");
            assertThat(intervals.size(), is(3));
            assertThat(intervals.get(0).getStart().toLocalDate(), is(new LocalDate(2012, 1, 1)));
            assertThat(intervals.get(2).getEnd().toLocalDate(), is(new LocalDate(2012, 10, 1)));
        }

        @Test
        public void intervalsInRangeTest2() {
            List<Interval> intervals = CalendarUtils.intervalsInRange(new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1), "RRULE:FREQ=MONTHLY;INTERVAL=3");
            assertThat(intervals.size(), is(1));
            assertThat(intervals.get(0).getStart().toLocalDate(), is(new LocalDate(2012, 1, 1)));
            assertThat(intervals.get(0).getEnd().toLocalDate(), is(new LocalDate(2012, 4, 1)));
        }

    }

    public static class NextDate extends CalendarUtilsTest {

        @Test
        public void nextDateTest() {
            assertThat(CalendarUtils.nextDate(new LocalDate(2012, 1, 1), "RRULE:FREQ=MONTHLY;INTERVAL=3"), is(new LocalDate(2012, 4, 1)));
        }

    }

}