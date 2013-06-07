package org.estatio.dom.lease;

import org.joda.time.LocalDate;

import org.estatio.dom.utils.CalendarUtils;
import org.estatio.dom.utils.StringUtils;


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

    // //////////////////////////////////////

    public LocalDate nextDate(LocalDate date) {
        return CalendarUtils.nextDate(date, this.rrule);
    }
    

}
