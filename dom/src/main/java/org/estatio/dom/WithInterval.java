package org.estatio.dom;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.valuetypes.LocalDateInterval;

public interface WithInterval {

    public LocalDate getStartDate();
    public LocalDate getEndDate();
    
    @Programmatic
    public LocalDateInterval getInterval();
}
