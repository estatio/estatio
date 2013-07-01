package org.estatio.dom;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

public interface WithIntervalMutable<T extends WithIntervalMutable<T>> extends WithInterval<T> {

    @MemberOrder(name="endDate", sequence="1")
    @ActionSemantics(Of.IDEMPOTENT)
    public T changeDates(
            final @Named("Start Date") LocalDate startDate, 
            final @Named("End Date") LocalDate endDate);

    public String disableChangeDates(
            final LocalDate startDate, 
            final LocalDate endDate);

    public LocalDate default0ChangeDates();
    public LocalDate default1ChangeDates();
    
    public String validateChangeDates(
            final LocalDate startDate, 
            final LocalDate endDate);


}
