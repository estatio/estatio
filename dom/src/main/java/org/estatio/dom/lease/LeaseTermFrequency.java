package org.estatio.dom.lease;

import org.estatio.dom.utils.CalendarUtils;
import org.joda.time.LocalDate;


public enum LeaseTermFrequency {

    YEARLY("Yearly", "RRULE:FREQ=YEARLY;INTERVAL=1"),
    NO_FREQUENCY("No Frequency", null);

    private String title;

    public String title() {
        return title;
    }

    private String rrule;

    public String rrule() {
        return rrule;
    }

    private LeaseTermFrequency(String title, String rrule) {
        this.rrule = rrule;
        this.title = title;
    }

    public LocalDate nextDate(LocalDate date) {
        return CalendarUtils.nextDate(date, this.rrule);
    }
}
