package org.estatio.dom.utils;

import com.google.ical.compat.jodatime.LocalDateIterator;
import com.google.ical.compat.jodatime.LocalDateIteratorFactory;

import org.joda.time.Interval;
import org.joda.time.LocalDate;

public class CalenderUtils {

    private CalenderUtils() {
    }

    public static Interval currentInterval(LocalDate date, String rrule) {
        return currentInterval(date, rrule, new LocalDate(2000, 1, 1));
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
        return date == null ? null : currentInterval(date, rrule).getEnd().toLocalDate();
    }

    public static boolean isBetween(LocalDate date, LocalDate startDate, LocalDate endDate) {
        if (date.compareTo(startDate) >= 0 && (endDate == null || date.compareTo(endDate) <= 0)) {
            return true;
        }
        return false;
    }

}
