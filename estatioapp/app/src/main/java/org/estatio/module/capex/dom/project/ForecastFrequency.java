package org.estatio.module.capex.dom.project;

import java.math.BigDecimal;

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

    public LocalDateInterval getIntervalFor(final LocalDate date) {
        return new LocalDateInterval(CalendarUtils.intervalContaining(date, rrule));
    }

    public static class Meta {
        private Meta() {}

        public final static int MAX_LEN = 20;

    }

}
