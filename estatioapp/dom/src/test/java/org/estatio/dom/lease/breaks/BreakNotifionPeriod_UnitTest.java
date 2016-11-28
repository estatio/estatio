/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.estatio.dom.lease.breaks;

import org.joda.time.LocalDate;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BreakNotifionPeriod_UnitTest {
    public static class AddTo extends BreakNotifionPeriod_UnitTest {

        @Test
        public void addTo() {
            assertAddTo(BreakNotificationPeriod.ONE_WEEK, new LocalDate(2013, 4, 3), new LocalDate(2013, 4, 10));
            assertAddTo(BreakNotificationPeriod.TWO_WEEKS, new LocalDate(2013, 4, 3), new LocalDate(2013, 4, 17));
            assertAddTo(BreakNotificationPeriod.ONE_MONTH, new LocalDate(2013, 4, 3), new LocalDate(2013, 5, 3));
            assertAddTo(BreakNotificationPeriod.TWO_MONTHS, new LocalDate(2013, 4, 3), new LocalDate(2013, 6, 3));
            assertAddTo(BreakNotificationPeriod.THREE_MONTHS, new LocalDate(2013, 4, 3), new LocalDate(2013, 7, 3));
            assertAddTo(BreakNotificationPeriod.SIX_MONTHS, new LocalDate(2013, 4, 3), new LocalDate(2013, 10, 3));
            assertAddTo(BreakNotificationPeriod.ONE_YEAR, new LocalDate(2013, 4, 3), new LocalDate(2014, 4, 3));
        }

        private static void assertAddTo(
                final BreakNotificationPeriod bnp, final LocalDate input, final LocalDate expected) {
            assertThat(bnp.addTo(input)).isEqualTo(expected);
        }

    }

    public static class SubtractFrom extends BreakNotifionPeriod_UnitTest {

        @Test
        public void subtractFrom() {
            assertSubtractFrom(BreakNotificationPeriod.ONE_WEEK, new LocalDate(2013, 4, 10), new LocalDate(2013, 4, 3));
            assertSubtractFrom(BreakNotificationPeriod.TWO_WEEKS, new LocalDate(2013, 4, 17), new LocalDate(2013, 4, 3));
            assertSubtractFrom(BreakNotificationPeriod.ONE_MONTH, new LocalDate(2013, 5, 3), new LocalDate(2013, 4, 3));
            assertSubtractFrom(BreakNotificationPeriod.TWO_MONTHS, new LocalDate(2013, 6, 3), new LocalDate(2013, 4, 3));
            assertSubtractFrom(BreakNotificationPeriod.THREE_MONTHS, new LocalDate(2013, 7, 3), new LocalDate(2013, 4, 3));
            assertSubtractFrom(BreakNotificationPeriod.SIX_MONTHS, new LocalDate(2013, 10, 3), new LocalDate(2013, 4, 3));
            assertSubtractFrom(BreakNotificationPeriod.ONE_YEAR, new LocalDate(2014, 4, 3), new LocalDate(2013, 4, 3));
        }

        private static void assertSubtractFrom(
                final BreakNotificationPeriod bnp, final LocalDate input, final LocalDate expected) {
            assertThat(bnp.subtractFrom(input)).isEqualTo(expected);
        }

    }
}