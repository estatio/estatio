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
package org.estatio.dom.utils;

import com.google.ical.compat.jodatime.LocalDateIterator;
import com.google.ical.compat.jodatime.LocalDateIteratorFactory;

import org.joda.time.Interval;
import org.joda.time.LocalDate;

public final class CalendarUtils {

    /**
     * TODO: EST-112
     */
    private static final LocalDate START_DATE_DEFAULT = new LocalDate(2000, 1, 1);

    private CalendarUtils() {
    }

    public static Interval intervalMatching(final LocalDate startDate, final String rrule) {
        Interval interval =  intervalContaining(startDate, rrule);
        if (interval.getStart().toLocalDate().equals(startDate)) {
            return interval;
        }
        return null;
    }
    
    public static Interval intervalContaining(final LocalDate containingDate, final String rrule) {
        return currentInterval(containingDate, rrule, START_DATE_DEFAULT);
    }
    
    public static Interval currentInterval(
            final LocalDate date, 
            final String rrule, 
            final LocalDate startDate) {
        if (date == null || startDate == null || rrule == null) {
            return null;
        }
        try {
            LocalDate thisDate = startDate;
            final LocalDateIterator iter = 
                    LocalDateIteratorFactory.createLocalDateIterator(rrule, thisDate, true);
            while (iter.hasNext()) {
                LocalDate nextDate = iter.next();
                if (nextDate.compareTo(date) > 0) {
                    return new Interval(
                            thisDate.toInterval().getStartMillis(), 
                            nextDate.toInterval().getStartMillis());
                }
                thisDate = nextDate;
            }
        } catch (Exception e) {
            // TODO - what's meant to happen here???
            e.printStackTrace();
        }
        return null;
    }

    public static LocalDate nextDate(final LocalDate date, final String rrule) {
        return date == null || rrule == null ? null : currentInterval(date, rrule, date).getEnd().toLocalDate();
    }

    public static boolean isBetween(final LocalDate date, final LocalDate startDate, final LocalDate endDate) {
        if (startDate != null && date.compareTo(startDate) >= 0 && (endDate == null || date.compareTo(endDate) <= 0)) {
            return true;
        }
        return false;
    }

}
