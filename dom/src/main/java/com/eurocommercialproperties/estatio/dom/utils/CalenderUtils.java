package com.eurocommercialproperties.estatio.dom.utils;

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
    
    public static Interval currentInterval(LocalDate date, String rrule, LocalDate startDate){
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

    public static LocalDate nextDate(LocalDate date, String rrule){
        return currentInterval(date, rrule).getEnd().toLocalDate();
    }
    
}
