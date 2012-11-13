package com.eurocommercialproperties.estatio.dom.utils;

import java.text.ParseException;

import org.joda.time.Interval;
import org.joda.time.LocalDate;

import com.google.ical.compat.jodatime.LocalDateIterator;
import com.google.ical.compat.jodatime.LocalDateIteratorFactory;
import com.google.ical.values.Frequency;
import com.google.ical.values.RRule;

public class CalenderUtils {

    private CalenderUtils() {
    }

    public static Interval currentInterval(LocalDate date, String rrule) throws ParseException {
        RRule rule = new RRule();
        rule.setFreq(Frequency.MONTHLY);
        rule.setInterval(3);

        LocalDate startDate;
        LocalDate endDate;
        LocalDate nextDate;

        

        startDate = new LocalDate(2000, 1, 1);
        LocalDateIterator iter = LocalDateIteratorFactory.createLocalDateIterator(rrule, startDate, true);

        while (iter.hasNext()) {
            nextDate = iter.next();
            if (nextDate.compareTo(date) > 0) {
                return new Interval(startDate.toInterval().getStartMillis(), nextDate.toInterval().getStartMillis());
            }
            startDate = nextDate;
        }
        return null;
    }

}
