package org.estatio.module.capex.dom.project;

import org.joda.time.Interval;
import org.joda.time.LocalDate;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.invoice.dom.InvoicingInterval;
import org.estatio.module.lease.dom.util.CalendarUtils;

public enum  ForecastFrequency {

    QUARTERLY("RRULE:FREQ=MONTHLY;INTERVAL=3");

    ForecastFrequency(
            final String rrule) {
        this.rrule = rrule;
    }

    private final String rrule;


    public LocalDate getStartDateFor(final LocalDate date) {
        Interval interval = CalendarUtils.intervalContaining(date, rrule);
        return interval.getStart().toLocalDate();
    }

    public LocalDate getNextStartDateFor(final LocalDate date) {
        Interval interval = CalendarUtils.intervalContaining(date, rrule);
        return interval.getEnd().toLocalDate();
    }

    public LocalDate getPreviousStartDateFor(final LocalDate date) {
        LocalDate startDate = getStartDateFor(date);
        return getStartDateFor(startDate.minusDays(1));
    }

    public LocalDateInterval getIntervalFor(final LocalDate date) {
        return new LocalDateInterval(CalendarUtils.intervalContaining(date, rrule));
    }

    public Quarter getQuarterFor(final LocalDate date){
        Interval interval = CalendarUtils.intervalContaining(date, rrule);
        switch (interval.getStart().toLocalDate().getMonthOfYear()){

        case 1:
        case 2:
        case 3:
            return Quarter.Q1;

        case 4:
        case 5:
        case 6:
            return Quarter.Q2;

        case 7:
        case 8:
        case 9:
            return Quarter.Q3;

        case 10:
        case 11:
        case 12:
            return Quarter.Q4;

        default:
            return null;
        }
    }

    public static LocalDate getStartDateForQuarter(final int year, final Quarter quarter){
        switch (quarter){
        case Q1:
            return new LocalDate(year,1,1);
        case Q2:
            return new LocalDate(year,4,1);
        case Q3:
            return new LocalDate(year,7,1);
        case Q4:
            return new LocalDate(year,10,1);
        default:
            return null;
        }
    }

    public static class Meta {
        private Meta() {}

        public final static int MAX_LEN = 20;

    }

    enum Quarter {
        Q1,
        Q2,
        Q3,
        Q4
    }

}
