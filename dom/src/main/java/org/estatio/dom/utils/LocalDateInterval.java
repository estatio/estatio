package org.estatio.dom.utils;

import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public final class LocalDateInterval {

    public enum IntervalEnding {
        INCLUDING_END_DATE, EXCLUDING_END_DATE
    }

    private long startInstant;
    private long endInstant;

    public LocalDateInterval(LocalDate startDate, LocalDate endDate) {
        this(startDate, endDate, IntervalEnding.EXCLUDING_END_DATE);
    }

    public LocalDateInterval(LocalDate startDate, LocalDate endDate, IntervalEnding ending) {
        startInstant = startDate == null ? Long.MIN_VALUE : startDate.toInterval().getStartMillis();
        endInstant = endDate == null ? Long.MAX_VALUE : ending == IntervalEnding.EXCLUDING_END_DATE ? endDate.toInterval().getStartMillis() : endDate.toInterval().getEndMillis();
    }

    public LocalDateInterval(Interval interval) {
        if (interval != null) {
            startInstant = interval.getStartMillis();
            endInstant = interval.getEndMillis();
        }
    }

    public Interval asInterval() {
        return new Interval(startInstant, endInstant);
    }

    public LocalDate getStartDate() {
        return new LocalDate(startInstant);
    }

    public LocalDate getEndDate() {
        return getEndDate(IntervalEnding.EXCLUDING_END_DATE);
    }

    public LocalDate getEndDate(IntervalEnding ending) {
        LocalDate returnDate = new LocalDate(endInstant);
        return ending == IntervalEnding.INCLUDING_END_DATE ? returnDate.minusDays(1) : returnDate;
    }

    /**
     * Does this time interval contain the specified time interval.
     * 
     * @param localDateInterval
     * @return
     */
    public boolean contains(LocalDateInterval localDateInterval) {
        return asInterval().contains(localDateInterval.asInterval());
    }

    /**
     * Does this date contain the specified time interval.
     * 
     * @param localDate
     * @return
     */
    public boolean contains(LocalDate localDate) {
        return asInterval().contains(localDate.toInterval());
    }

    /**
     * Does this time interval contain the specified time interval.
     * 
     * @param interval
     * @return
     */
    public boolean overlaps(LocalDateInterval interval) {
        return asInterval().overlaps(interval.asInterval());
    }

    /**
     * Gets the overlap between this interval and another interval.
     * 
     * @param interval
     * @return
     */
    public LocalDateInterval overlap(LocalDateInterval interval) {
        Interval interval2 = asInterval().overlap(interval.asInterval());
        if (interval2 == null)
            return null;
        return new LocalDateInterval(interval2);
    }

    /**
     * Does this interval is within the specified interval
     * 
     * @param interval
     * @return
     */
    public boolean within(LocalDateInterval interval) {
        return interval.asInterval().contains(asInterval());
    }

    public int getDays() {
        Period p = new Period(asInterval(), PeriodType.days());
        return p.getDays();
    }

}
