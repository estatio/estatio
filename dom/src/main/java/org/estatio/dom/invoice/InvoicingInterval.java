package org.estatio.dom.invoice;

import org.joda.time.Interval;
import org.joda.time.LocalDate;

import org.estatio.dom.valuetypes.LocalDateInterval;

public class InvoicingInterval extends LocalDateInterval {

    private LocalDate dueDate;

    public LocalDate dueDate() {
        return this.dueDate;
    }

    public InvoicingInterval(final Interval interval, final LocalDate dueDate) {
        super(interval);
        this.dueDate = dueDate;
    }

    public InvoicingInterval(final LocalDateInterval interval, final LocalDate dueDate) {
        super(interval.startDate(), interval.endDateExcluding(), IntervalEnding.EXCLUDING_END_DATE);
        this.dueDate = dueDate;

    }

}
