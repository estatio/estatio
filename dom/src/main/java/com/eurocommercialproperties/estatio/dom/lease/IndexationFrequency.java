package com.eurocommercialproperties.estatio.dom.lease;

import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.utils.CalenderUtils;

public enum IndexationFrequency {

    YEARLY("Weekly", "RRULE:FREQ=YEARLY;INTERVAL=1");

    private String title;

    public String title() {
        return title;
    }

    private String rrule;

    public String rrule() {
        return rrule;
    }

    private IndexationFrequency(String title, String rrule) {
        this.rrule = rrule;
        this.title = title;
    }

    public LocalDate nextDate(LocalDate date) {
        return CalenderUtils.nextDate(date, this.rrule);
    }
}
