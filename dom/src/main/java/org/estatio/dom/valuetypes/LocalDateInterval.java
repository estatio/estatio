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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public final class LocalDateInterval {

    private Long startInstant;
    private Long endInstant;
    private static final IntervalEnding PERSISTENT_ENDING = IntervalEnding.INCLUDING_END_DATE;

    public enum IntervalEnding {
        INCLUDING_END_DATE, EXCLUDING_END_DATE
    }

    public static LocalDateInterval excluding(final LocalDate startDate, final LocalDate endDate) {
        return new LocalDateInterval(startDate, endDate, IntervalEnding.EXCLUDING_END_DATE);
    }

    public static LocalDateInterval including(final LocalDate startDate, final LocalDate endDate) {
        return new LocalDateInterval(startDate, endDate, IntervalEnding.INCLUDING_END_DATE);
    }

    public LocalDateInterval(final LocalDate startDate, final LocalDate endDate, final IntervalEnding ending) {
        startInstant = startDate == null ? null : startDate.toInterval().getStartMillis();
        endInstant = endDate == null
                ? null
                : ending == IntervalEnding.EXCLUDING_END_DATE
                        ? endDate.toInterval().getStartMillis()
                        : endDate.toInterval().getEndMillis();
    }

    public LocalDateInterval() {
    }

    public LocalDateInterval(final Interval interval) {
        if (interval != null) {
            startInstant = interval.getStartMillis();
            endInstant = interval.getEndMillis();
        }
    }

    public Interval asInterval() {
        return new Interval(
                startInstant == null ? 0 : startInstant,
                endInstant == null ? Long.MAX_VALUE : endInstant);
    }

    public LocalDate startDate() {
        if (startInstant == null) {
            return null;
        }
        return new LocalDate(startInstant);
    }

    public LocalDate endDate() {
        return endDate(PERSISTENT_ENDING);
    }

    public LocalDate endDate(final IntervalEnding ending) {
        if (endInstant == null) {
            return null;
        }
        LocalDate date = new LocalDate(endInstant);
        return adjustDate(date, ending);
    }

    public LocalDate endDateExcluding() {
        return endDate(IntervalEnding.EXCLUDING_END_DATE);
    }

    public LocalDate endDateFromStartDate() {
        return adjustDate(startDate(), PERSISTENT_ENDING);
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
        if (otherInterval == null || otherInterval.isInfinite()) {
            return this;
        }
        final Interval thisAsInterval = asInterval();
        final Interval otherAsInterval = otherInterval.asInterval();
        Interval overlap = thisAsInterval.overlap(otherAsInterval);
        if (overlap == null) {
            return new LocalDateInterval();
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

    /**
     * The duration in days
     * 
     * @return
     */
    public int days() {
        if (isInfinite()){
            return 0;
        }
        Period p = new Period(asInterval(), PeriodType.days());
        return p.getDays();
    }

    private boolean isInfinite() {
        return startInstant == null && endInstant == null;
    }

    @Override
    public String toString() {
        StringBuilder builder =
                new StringBuilder(
                        startDate() == null ? "----------" : startDate().toString("yyyy-MM-dd")).append("/").append(
                        endDate() == null ? "----------" : endDate().toString("yyyy-MM-dd"));
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(16, 23). // two randomly chosen prime numbers
                append(startInstant).
                append(endInstant).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof LocalDateInterval))
            return false;
        LocalDateInterval rhs = (LocalDateInterval) obj;
        return new EqualsBuilder().
                append(startInstant, rhs.startInstant).
                append(endInstant, rhs.endInstant).
                isEquals();
    }
}
