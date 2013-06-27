package org.estatio.dom;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.valuetypes.LocalDateInterval;

public interface WithInterval extends WithStartDate {

    @Optional
    @Disabled
    public LocalDate getEndDate();
    
    public void setEndDate(LocalDate localDate);
    
    @Programmatic
    public LocalDateInterval getInterval();
}
