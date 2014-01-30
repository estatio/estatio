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
package org.estatio.dom.valuetypes;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public class LocalDateInterval {

    public enum IntervalEnding {
        EXCLUDING_END_DATE, INCLUDING_END_DATE
    }

    private static class IntervalUtil {
        private static final long MIN_VALUE = 0;
        private static final long MAX_VALUE = Long.MAX_VALUE;

        public static Interval toInterval(final LocalDateInterval localDateInterval) {
            Long startInstant = toStartInstant(localDateInterval.startDate());
            Long endInstant = toEndInstant(localDateInterval.endDateExcluding());
            return new Interval(startInstant, endInstant);
        }

        public static LocalDate toLocalDate(final long instant) {
            if (instant == MAX_VALUE || instant == MIN_VALUE) {
                return null;
            }
            return new LocalDate(instant);
        }

        private static long toStartInstant(final LocalDate date) {
            if (date == null) {
                return MIN_VALUE;
            }
            return date.toInterval().getStartMillis();
        }

        private static long toEndInstant(final LocalDate date) {
            if (date == null) {
                return MAX_VALUE;
            }
            return date.toInterval().getStartMillis();
        }

    }

    private static final IntervalEnding PERSISTENT_ENDING = IntervalEnding.INCLUDING_END_DATE;

    public static LocalDateInterval excluding(final LocalDate startDate, final LocalDate endDate) {
        return new LocalDateInterval(startDate, endDate, IntervalEnding.EXCLUDING_END_DATE);
    }

    public static LocalDateInterval including(final LocalDate startDate, final LocalDate endDate) {
        return new LocalDateInterval(startDate, endDate, IntervalEnding.INCLUDING_END_DATE);
    }

    private LocalDate endDate;
    private LocalDate startDate;

    public LocalDateInterval() {
    }

    public LocalDateInterval(final Interval interval) {
        if (interval == null) {
            throw new IllegalArgumentException("interval cannot be null");
        }
        startDate = IntervalUtil.toLocalDate(interval.getStartMillis());
        endDate = IntervalUtil.toLocalDate(interval.getEndMillis());
    }

    public LocalDateInterval(final LocalDate startDate, final LocalDate endDate) {
        this(startDate, endDate, PERSISTENT_ENDING);
    }

    public LocalDateInterval(final LocalDate startDate, final LocalDate endDate, final IntervalEnding ending) {
        this.startDate = startDate;
        this.endDate = adjustDateIn(endDate, ending);
    }

    public Interval asInterval() {
        return IntervalUtil.toInterval(this);
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
     * @param localDateInterval
     * @return
     */
    public boolean contains(final LocalDateInterval localDateInterval) {
        return asInterval().contains(localDateInterval.asInterval());
    }

    /**
     * The duration in days
     * 
     * @return
     */
    public int days() {
        if (isInfinite()) {
            return 0;
        }
        Period p = new Period(asInterval(), PeriodType.days());
        return p.getDays();
    }

    public LocalDate endDate() {
        return endDate(PERSISTENT_ENDING);
    }

    public LocalDate endDate(final IntervalEnding ending) {
        if (endDate == null) {
            return null;
        }
        return adjustDateOut(endDate, ending);
    }

    public LocalDate endDateExcluding() {
        return endDate(IntervalEnding.EXCLUDING_END_DATE);
    }

    public LocalDate endDateFromStartDate() {
        return adjustDateOut(startDate(), PERSISTENT_ENDING);
    }

    @Override
    public boolean equals(final Object obj) {
        // TODO: use Isis' ObjectContracts?
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LocalDateInterval)) {
            return false;
        }
        LocalDateInterval rhs = (LocalDateInterval) obj;
        return new EqualsBuilder().
                append(startDate, rhs.startDate).
                append(endDate, rhs.endDate).
                isEquals();
    }

    public boolean isValid() {
        return startDate == null || endDate == null || endDate.isAfter(startDate) || endDate.equals(startDate);
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
        if (otherInterval.isInfinite()) {
            return this;
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
     * Does this time interval contain the specified time interval.
     * 
     * @param interval
     * @return
     */
    public boolean overlaps(final LocalDateInterval interval) {
        return asInterval().overlaps(interval.asInterval());
    }

    public LocalDate startDate() {
        return startDate;
    }

    @Override
    public String toString() {
        StringBuilder builder =
                new StringBuilder(
                        startDate() == null ? "----------" : startDate().toString("yyyy-MM-dd")).append("/").append(
                        endDateExcluding() == null ? "----------" : endDateExcluding().toString("yyyy-MM-dd"));
        return builder.toString();
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

    private LocalDate adjustDateIn(final LocalDate date, final IntervalEnding ending) {
        if (date == null) {
            return null;
        }
        return ending == IntervalEnding.INCLUDING_END_DATE ? date.plusDays(1) : date;
    }

    private LocalDate adjustDateOut(final LocalDate date, final IntervalEnding ending) {
        if (date == null) {
            return null;
        }
        return ending == IntervalEnding.INCLUDING_END_DATE ? date.minusDays(1) : date;
    }

    private boolean isInfinite() {
        return startDate == null && endDate == null;
    }

    /**
     * Parse a string representation of a LocalDateInterval
     * 
     * Since this method is only used for testing it's not heavily guarded against illegal arguments
     * 
     * @param input  a string with format yyyy-mm-dd/yyyy-mm-dd, end date is excluding
     * @return
     */
    public static LocalDateInterval parseString(final String input) {
        String[] values = input.split("/");
        try {
            return new LocalDateInterval(parseLocalDate(values[0]), parseLocalDate(values[1]), IntervalEnding.EXCLUDING_END_DATE);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to parse " + input);
        }
    }

    /**
     * Parse a string to a LocalDate
     * 
     * @param input  a string representing a parsable LocalDate, "*" or "----------" returns null
     * @return
     */
    private static LocalDate parseLocalDate(final String input) {
        if (input.contains("--") || input.contains("*")) {
            return null;
        }
        return LocalDate.parse(input);

    }
}
