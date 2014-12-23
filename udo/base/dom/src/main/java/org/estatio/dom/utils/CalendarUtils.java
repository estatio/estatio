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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.ical.compat.jodatime.LocalDateIterator;
import com.google.ical.compat.jodatime.LocalDateIteratorFactory;

import org.joda.time.Interval;
import org.joda.time.LocalDate;

import org.estatio.dom.EstatioApplicationException;
import org.estatio.dom.valuetypes.LocalDateInterval;

public final class CalendarUtils {

    /**
     * TODO: EST-112
     */
    private static final LocalDate START_DATE_DEFAULT = new LocalDate(2000, 1, 1);

    private CalendarUtils() {
    }

    /**
     * Returns an interval based on rrule which start date matches startDate
     * 
     * @param startDate
     * @param rrule
     * @return
     */
    public static Interval intervalMatching(final LocalDate startDate, final String rrule) {
        Interval interval = intervalContaining(startDate, rrule);
        if (interval.getStart().toLocalDate().equals(startDate)) {
            return interval;
        }
        return null;
    }

    /**
     * Returns an interval based on rrule which contains containgDate
     * 
     * @param containingDate
     * @param rrule
     * @return
     */
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
        } catch (final ParseException ex) {
            throw new EstatioApplicationException("Unable to parse rrule >>" + rrule + "<<", ex);
        }
        return null;
    }

    /**
     * Returns the start date of the next interval
     * 
     * @param date
     * @param rrule
     * @return
     */
    public static LocalDate nextDate(final LocalDate date, final String rrule) {
        return date == null || rrule == null ? null : currentInterval(date, rrule, date).getEnd().toLocalDate();
    }

    public static boolean isBetween(final LocalDate date, final LocalDate startDate, final LocalDate endDate) {
        if (startDate != null && date.compareTo(startDate) >= 0 && (endDate == null || date.compareTo(endDate) <= 0)) {
            return true;
        }
        return false;
    }

    public static List<Interval> intervalsInRange(
            final LocalDate startDate,
            final LocalDate endDate,
            final String rrule) {
        if (startDate.compareTo(endDate) > 0) {
            throw new IllegalArgumentException(
                    String.format("Start date %s is after end date %s", startDate.toString(), endDate.toString()));
        }
        List<Interval> intervals = Lists.newArrayList();
        LocalDate start = startDate;
        Interval interval = null;
        do {
            interval = intervalContaining(start, rrule);
            if (interval != null) {
                intervals.add(interval);
                start = interval.getEnd().toLocalDate();
            }
        } while (interval != null && start.isBefore(endDate));
        return intervals;
    }

    public static List<LocalDateInterval> localDateIintervalsInRange(
            final LocalDate startDate,
            final LocalDate endDate,
            final String rrule) {
        List<LocalDateInterval> localDateIntervals = new ArrayList<LocalDateInterval>();
        for (Interval interval : intervalsInRange(startDate, endDate, rrule)) {
            localDateIntervals.add(new LocalDateInterval(interval));
        }
        return localDateIntervals;
    }

}
