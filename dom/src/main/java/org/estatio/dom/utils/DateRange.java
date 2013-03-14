package org.estatio.dom.utils;

import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public final class DateRange {

    private LocalDate startDate;
    private LocalDate endDate;
    private DateRange boundingRange;

    public DateRange(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }


    public DateRange(LocalDate startDate, LocalDate endDate, boolean inclusiveEndDate) {
        this.startDate = startDate;
        if (endDate != null) {
            this.endDate = endDate.plusDays(inclusiveEndDate ? 1 : 0);
        }
    }

    public DateRange(Interval interval) {
        this.setStartDate(interval.getStart().toLocalDate());
        this.setEndDate(interval.getEnd().toLocalDate());
    }

    public LocalDate getStartDate() {
        return startDate == null ? new LocalDate(1900, 1, 1) : startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate == null ? new LocalDate(2999, 1, 1) : endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setBoundingRange(DateRange boundingRange) {
        this.boundingRange = boundingRange;
    }

    public LocalDate getActualStartDate() {
        if (isWithinParent()) {
            return getStartDate().compareTo(boundingRange.getStartDate()) < 0 ? boundingRange.getStartDate() : getStartDate();
        }
        return null;
    }

    public LocalDate getActualEndDate() {
        if (isWithinParent()) {
            return getEndDate().compareTo(boundingRange.getEndDate()) > 0 ? boundingRange.getEndDate() : getEndDate();
        }
        return null;
    }

    public boolean isFullInterval() {
        return (getStartDate().compareTo(boundingRange.getStartDate()) <= 0 && getEndDate().compareTo(boundingRange.getEndDate()) >= 0);
    }

    public boolean isWithinParent() {
        return !(getStartDate().compareTo(boundingRange.getEndDate()) >= 0 || this.getEndDate().compareTo(boundingRange.getStartDate()) <= 0);
    }

    public int getDays() {
        Period p = new Period(this.getStartDate(), this.getEndDate(), PeriodType.days());
        return p.getDays();
    }

    public int getActualDays() {
        try {
            Days d = Days.daysBetween(getActualStartDate(), getActualEndDate());
            int days = d.getDays();
            return days;
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }

    public boolean contains(LocalDate localDate) {
        return getStartDate().compareTo(localDate) <= 0 && getEndDate().compareTo(localDate) >= 0;
    }

}
