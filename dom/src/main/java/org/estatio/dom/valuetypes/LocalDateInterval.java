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

import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public final class LocalDateInterval {

    private static final long OPEN_START_INSTANT = Long.MIN_VALUE;
    private static final long OPEN_END_INSTANT = Long.MAX_VALUE;
    private long startInstant;
    private long endInstant;
    private IntervalEnding ending = IntervalEnding.INCLUDING_END_DATE;

    private enum IntervalEnding {
        INCLUDING_END_DATE, EXCLUDING_END_DATE
    }

    public static LocalDateInterval excluding(final LocalDate startDate, final LocalDate endDate) {
        return new LocalDateInterval(startDate, endDate, IntervalEnding.EXCLUDING_END_DATE);
    }

    public static LocalDateInterval including(final LocalDate startDate, final LocalDate endDate) {
        return new LocalDateInterval(startDate, endDate, IntervalEnding.INCLUDING_END_DATE);
    }

    public LocalDateInterval(final LocalDate startDate, final LocalDate endDate, final IntervalEnding ending) {
        this.ending = ending;
        startInstant = startDate == null ? OPEN_START_INSTANT : startDate.toInterval().getStartMillis();
        endInstant = endDate == null
                ? OPEN_END_INSTANT
                : ending == IntervalEnding.EXCLUDING_END_DATE
                        ? endDate.toInterval().getStartMillis()
                        : endDate.toInterval().getEndMillis();
    }

    public LocalDateInterval(final Interval interval) {
        if (interval != null) {
            startInstant = interval.getStartMillis();
            endInstant = interval.getEndMillis();
        }
    }

    public Interval asInterval() {
        return new Interval(startInstant, endInstant);
    }

    public LocalDate startDate() {
        if (startInstant == OPEN_START_INSTANT || startInstant == 0) {
            return null;
        }
        return new LocalDate(startInstant);
    }

    public LocalDate endDate() {
        return endDate(ending);
    }

    public LocalDate endDate(final IntervalEnding ending) {
        if (endInstant == OPEN_END_INSTANT || endInstant == 0) {
            return null;
        }
        LocalDate date = new LocalDate(endInstant);
        return adjustDate(date, ending);
    }

    public LocalDate endDateExcluding() {
        return endDate(IntervalEnding.EXCLUDING_END_DATE);
    }

    public LocalDate endDateFromStartDate() {
        return adjustDate(startDate(), ending);
    }

    private LocalDate adjustDate(final LocalDate date, final IntervalEnding ending) {
        return ending == IntervalEnding.INCLUDING_END_DATE ? date.minusDays(1) : date;

    }

    /**
     * Does this time interval contain the specified time interval.
     * 
     * @param localDateInterval
     * @return
     */
    public boolean contains(final LocalDateInterval localDateInterval) {
        return asInterval().contains(localDateInterval.asInterval());
    }

    /**
     * Does this date contain the specified time interval.
     * 
     * @param date
     * @return
     */
    public boolean contains(final LocalDate date) {
        if (endDate() == null) {
            if (startDate() == null) {
                return true;
            }
            if (date.isEqual(startDate()) || date.isAfter(startDate())) {
                return true;
            }
            return false;
        }
        return asInterval().contains(date.toInterval());
    }

    /**
     * Does this time interval contain the specified time interval.
     * 
     * @param interval
     * @return
     */
    public boolean overlaps(final LocalDateInterval interval) {
        return asInterval().overlaps(interval.asInterval());
    }

    /**
     * Gets the overlap between this interval and another interval.
     * 
     * @param otherInterval
     * @return
     */
    public LocalDateInterval overlap(final LocalDateInterval otherInterval) {
        if (otherInterval == null) {
            return null;
        }
        final Interval thisAsInterval = asInterval();
        final Interval otherAsInterval = otherInterval.asInterval();
        Interval overlap = thisAsInterval.overlap(otherAsInterval);
        if (overlap == null) {
            return null;
        }
        return new LocalDateInterval(overlap);
    }

    /**
     * Does this interval is within the specified interval
     * 
     * @param interval
     * @return
     */
    public boolean within(final LocalDateInterval interval) {
        return interval.asInterval().contains(asInterval());
    }

    public int days() {
        Period p = new Period(asInterval(), PeriodType.days());
        return p.getDays();
    }

}
