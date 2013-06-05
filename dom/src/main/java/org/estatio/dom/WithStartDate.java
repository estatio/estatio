package org.estatio.dom;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.valuetypes.LocalDateInterval;

public interface WithStartDate {

    public LocalDate getStartDate();
    public void setStartDate(LocalDate localDate);
}
