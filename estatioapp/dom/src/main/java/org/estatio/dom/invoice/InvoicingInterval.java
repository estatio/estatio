package org.estatio.dom.invoice;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import org.estatio.dom.valuetypes.AbstractInterval;
import org.estatio.dom.valuetypes.LocalDateInterval;

public class InvoicingInterval extends AbstractInterval<InvoicingInterval> {

    // //////////////////////////////////////

    public InvoicingInterval() {
    }

    public InvoicingInterval(final Interval interval) {
        this(interval, null);
    }

    public InvoicingInterval(final Interval interval, final LocalDate dueDate) {
        super(interval);
        this.dueDate = dueDate;
    }

    public InvoicingInterval(final LocalDateInterval interval, final LocalDate dueDate) {
        super(interval.startDate(), interval.endDateExcluding(), IntervalEnding.EXCLUDING_END_DATE);
        this.dueDate = dueDate;
    }

    @Override
    protected InvoicingInterval newInterval(Interval overlap) {
        return new InvoicingInterval(overlap);
    }

    // //////////////////////////////////////

    private LocalDate dueDate;

    public LocalDate dueDate() {
        return this.dueDate;
    }

    // //////////////////////////////////////
    
    public LocalDateInterval asLocalDateInterval() {
        return new LocalDateInterval(startDate, endDate, IntervalEnding.EXCLUDING_END_DATE);
    }

    // //////////////////////////////////////

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof InvoicingInterval)) {
            return false;
        }
        InvoicingInterval rhs = (InvoicingInterval) obj;
        return new EqualsBuilder().
                append(startDate, rhs.startDate).
                append(endDate, rhs.endDate).
                append(dueDate, rhs.dueDate).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(startDate).append(endDate).append(dueDate).hashCode();
    }

    @Override
    public String toString() {
        return toString("yyyy-MM-dd");
    }

    public String toString(String format) {
        StringBuilder builder =
                new StringBuilder(
                        dateToString(startDate))
                        .append("/")
                        .append(dateToString(endDateExcluding(), format))
                        .append(":")
                        .append(dateToString(dueDate, format));
        ;
        return builder.toString();
    }


    // //////////////////////////////////////

}
