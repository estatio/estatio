package org.estatio.services.clock;

import org.joda.time.LocalDate;

import org.apache.isis.applib.clock.Clock;

public class ClockService {

    public LocalDate now() {
        return Clock.getTimeAsLocalDate();
    }

}
