package org.estatio.dom;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.valuetypes.LocalDateInterval;

public interface WithInterval<T extends WithInterval<T>> extends WithStartDate {

    
    @Optional
    @Disabled
    public LocalDate getEndDate();
    public void setEndDate(LocalDate endDate);
    
    @Programmatic
    public LocalDateInterval getInterval();


    /**
     * The interval that immediately precedes this one, if any.
     * 
     * <p>
     * The predecessor's {@link #getEndDate() end date} is the day before this interval's
     * {@link #getStartDate() start date}.
     */
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @Optional
    public T getPrevious();

    /**
     * The interval that immediately succeeds this one, if any.
     * 
     * <p>
     * The successor's {@link #getStartDate() start date} is the day after this interval's
     * {@link #getEndDate() end date}.
     */
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @Optional
    public T getNext();

}
