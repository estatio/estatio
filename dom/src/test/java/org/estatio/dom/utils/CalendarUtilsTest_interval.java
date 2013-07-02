/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.estatio.dom.valuetypes.LocalDateInterval;
import org.hamcrest.core.Is;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.junit.Test;

public class CalendarUtilsTest_interval {

    @Test
    public void intervalContainingTest() throws Exception {
        assertEquals(CalendarUtils.intervalContaining(new LocalDate(2012, 2, 1), "RRULE:FREQ=MONTHLY;INTERVAL=3"), new Interval(new LocalDate(2012, 1, 1).toInterval().getStartMillis(), new LocalDate(2012, 4, 1).toInterval().getStartMillis()));
    }

    @Test
    public void intervalMatchingTest() throws Exception {
        assertEquals(CalendarUtils.intervalMatching(new LocalDate(2012, 1, 1), "RRULE:FREQ=MONTHLY;INTERVAL=3"), new Interval(new LocalDate(2012, 1, 1).toInterval().getStartMillis(), new LocalDate(2012, 4, 1).toInterval().getStartMillis()));
        assertEquals(CalendarUtils.intervalMatching(new LocalDate(2012, 1, 1), "RRULE:FREQ=YEARLY;INTERVAL=1"), new Interval(new LocalDate(2012, 1, 1).toInterval().getStartMillis(), new LocalDate(2013, 1, 1).toInterval().getStartMillis()));
    }

    @Test
    public void intervalMatchingNotExactDateTest() throws Exception {
        assertNull(CalendarUtils.intervalMatching(new LocalDate(2012, 1, 2), "RRULE:FREQ=MONTHLY;INTERVAL=3"));
    }

    @Test
    public void zaraIntervalTest() throws Exception {
        assertThat(CalendarUtils.currentInterval(new LocalDate(2012, 3, 1), "RRULE:FREQ=MONTHLY;INTERVAL=3", new LocalDate(2010, 2, 1)), Is.is(LocalDateInterval.excluding(new LocalDate(2012, 2, 1), new LocalDate(2012, 5, 1)).asInterval()));
    }

    @Test
    public void zaraIntervalTest2() throws Exception {
        assertEquals(CalendarUtils.currentInterval(new LocalDate(2012, 3, 1), "RRULE:FREQ=MONTHLY;INTERVAL=3;BYMONTH=2,5,8,11", new LocalDate(2010, 1, 1)), new Interval(new LocalDate(2012, 2, 1).toInterval().getStartMillis(), new LocalDate(2012, 5, 1).toInterval().getStartMillis()));
    }

    @Test
    public void roundDown() throws Exception {
        assertThat(new BigDecimal("4.5"), is(MathUtils.round(new BigDecimal("4.54"), 1)));
    }

    @Test
    public void noRounding() throws Exception {
        assertThat(new BigDecimal("4.54"), is(MathUtils.round(new BigDecimal("4.54"), 2)));
    }

}
