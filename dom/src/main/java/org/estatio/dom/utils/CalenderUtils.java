package org.estatio.dom.utils;

import com.google.ical.compat.jodatime.LocalDateIterator;
import com.google.ical.compat.jodatime.LocalDateIteratorFactory;

import org.joda.time.Interval;
import org.joda.time.LocalDate;

public class CalenderUtils {

    private static final LocalDate START_DATE = new LocalDate(2000, 1, 1);

    private CalenderUtils() {
    }

    public static Interval intervalMatching(LocalDate date, String rrule) {
        Interval interval =  currentInterval(date, rrule, START_DATE);
        if (interval.getStart().toLocalDate().equals(date)) {
            return interval;
        }
        return null;
    }
    
    public static Interval intervalContaining(LocalDate date, String rrule) {
        return currentInterval(date, rrule, START_DATE);
    }
    
    public static Interval currentInterval(LocalDate date, String rrule, LocalDate startDate) {
        LocalDate nextDate;

        if (date == null || startDate == null || rrule == null)
        {
            return null;
        }

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
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static LocalDate nextDate(LocalDate date, String rrule) {
        return date == null ? null : intervalContaining(date, rrule).getEnd().toLocalDate();
    }

    public static boolean isBetween(LocalDate date, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && date.compareTo(startDate) >= 0 && (endDate == null || date.compareTo(endDate) <= 0)) {
            return true;
        }
        return false;
    }

}
