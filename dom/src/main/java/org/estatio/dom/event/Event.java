package org.estatio.dom.event;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithDescriptionGetter;
import org.estatio.dom.WithInterval;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.estatio.services.clock.ClockService;

// TODO: is this in scope?

//@PersistenceCapable
//@javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column="VERSION")
public class Event extends EstatioTransactionalObject<Event> implements WithInterval<Event>, WithDescriptionGetter {

    public Event() {
        super("startDate desc, id");
    }
    
    // //////////////////////////////////////

    private LocalDate startDate;

    @MemberOrder(name="Dates", sequence = "1")
    @Optional
    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }
    
    @Override
    public void modifyStartDate(final LocalDate startDate) {
        final LocalDate currentStartDate = getStartDate();
        if (startDate == null || startDate.equals(currentStartDate)) {
            return;
        }
        setStartDate(startDate);
    }

    @Override
    public void clearStartDate() {
        LocalDate currentStartDate = getStartDate();
        if (currentStartDate == null) {
            return;
        }
        setStartDate(null);
    }


    public LocalDate defaultStartDate() {
        return clockService.now();
    }
    
    // //////////////////////////////////////

    private LocalDate endDate;

    @MemberOrder(name="Dates", sequence = "1")
    @Disabled
    @Optional
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }
    
    @Override
    public void modifyEndDate(final LocalDate endDate) {
        final LocalDate currentEndDate = getEndDate();
        if (endDate == null || endDate.equals(currentEndDate)) {
            return;
        }
        setEndDate(endDate);
    }

    @Override
    public void clearEndDate() {
        LocalDate currentEndDate = getEndDate();
        if (currentEndDate == null) {
            return;
        }
        setEndDate(null);
    }


    // //////////////////////////////////////

    @Override
    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    // //////////////////////////////////////

    @Hidden // TODO
    @Disabled
    @Optional
    @Override
    public Event getPrevious() {
        return null;
    }

    @Hidden // TODO
    @Disabled
    @Optional
    @Override
    public Event getNext() {
        return null;
    }


    // //////////////////////////////////////

    private String description;

    @MemberOrder(sequence = "1")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // //////////////////////////////////////

    private ClockService clockService;
    public void injectClockService(final ClockService clockService) {
        this.clockService = clockService;
    }



}
