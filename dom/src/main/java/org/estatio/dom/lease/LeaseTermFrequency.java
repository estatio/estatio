package org.estatio.dom.lease;

import com.google.common.collect.Ordering;

import org.estatio.dom.utils.CalendarUtils;
import org.estatio.dom.utils.StringUtils;

import org.joda.time.LocalDate;


public enum LeaseTermFrequency {

    YEARLY("RRULE:FREQ=YEARLY;INTERVAL=1"),
    NO_FREQUENCY(null);

    private LeaseTermFrequency(String rrule) {
        this.rrule = rrule;
    }
    
    
    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    private String rrule;

    public String rrule() {
        return rrule;
    }

    public LocalDate nextDate(LocalDate date) {
        return CalendarUtils.nextDate(date, this.rrule);
    }
    
    public static Ordering<LeaseTermFrequency> ORDERING_BY_TYPE = 
            Ordering.<LeaseTermFrequency> natural().nullsFirst();

}
