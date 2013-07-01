package org.estatio.dom;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Optional;

public interface WithStartDate {

    @Optional
    public LocalDate getStartDate();
    public void setStartDate(LocalDate startDate);

}
