package org.estatio.dom.utils;

import java.text.ParseException;

import org.joda.time.Interval;
import org.joda.time.LocalDate;

import com.google.ical.compat.jodatime.LocalDateIterator;
import com.google.ical.compat.jodatime.LocalDateIteratorFactory;

public class CalenderUtils {

    private CalenderUtils() {
    }

    public static Interval currentInterval(LocalDate date, String rrule) {
        return currentInterval(date, rrule, new LocalDate(2000, 1, 1));
    }

    public static Interval currentInterval(LocalDate date, String rrule, LocalDate startDate) {
        LocalDate nextDate;

        LocalDateIterator iter;
        try {
            iter = LocalDateIteratorFactory.createLocalDateIterator(rrule, startDate, true);
            while (iter.hasNext()) {
                nextDate = iter.next();
                if (nextDate.compareTo(date) > 0) {
                    return new Interval(startDate.toInterval().getStartMillis(), nextDate.toInterval().getStartMillis());
                }
                startDate = nextDate;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static LocalDate nextDate(LocalDate date, String rrule) {
        return currentInterval(date, rrule).getEnd().toLocalDate();
    }

    public static boolean isBetween(LocalDate date, LocalDate startDate, LocalDate endDate) {
        if (date.compareTo(startDate) >= 0 && (endDate == null || date.compareTo(endDate) <= 0)) {
            return true;
        }
        ;
        return false;
    }

    public class MyInterval {

        private LocalDate startDate;
        private LocalDate endDate;
        private MyInterval parentInterval;

        public MyInterval(LocalDate startDate, LocalDate endDate) {
            super();
            this.startDate = startDate;
            this.endDate = endDate;
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

        public void setParentInterval(MyInterval parentInterval) {
            this.parentInterval = parentInterval;
        }

        public boolean isFullInterval() {
            return (getStartDate().compareTo(parentInterval.getStartDate()) <= 0 && getEndDate().compareTo(parentInterval.getEndDate()) >= 0);
        }

        public boolean isWithinParent() {
            return !(getStartDate().compareTo(parentInterval.getEndDate()) >= 0 || this.getEndDate().compareTo(parentInterval.getStartDate()) <= 0);

        }

    }
}
