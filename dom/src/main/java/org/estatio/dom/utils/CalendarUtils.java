package org.estatio.dom.utils;

import com.google.ical.compat.jodatime.LocalDateIterator;
import com.google.ical.compat.jodatime.LocalDateIteratorFactory;

import org.joda.time.Interval;
import org.joda.time.LocalDate;

import org.estatio.services.appsettings.EstatioSettingsService;

public class CalendarUtils {

    /**
     * TODO: EST-112
     */
    private static final LocalDate START_DATE_DEFAULT = new LocalDate(2000, 1, 1);

    private CalendarUtils() {
    }

    public static Interval intervalMatching(LocalDate startDate, String rrule) {
        Interval interval =  intervalContaining(startDate, rrule);
        if (interval.getStart().toLocalDate().equals(startDate)) {
            return interval;
        }
        return null;
    }
    
    public static Interval intervalContaining(LocalDate containingDate, String rrule) {
        return currentInterval(containingDate, rrule, START_DATE_DEFAULT);
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
        return date == null || rrule == null ? null : currentInterval(date, rrule, date).getEnd().toLocalDate();
    }

    public static boolean isBetween(LocalDate date, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && date.compareTo(startDate) >= 0 && (endDate == null || date.compareTo(endDate) <= 0)) {
            return true;
        }
        return false;
    }

}
