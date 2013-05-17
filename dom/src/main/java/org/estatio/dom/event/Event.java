package org.estatio.dom.event;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.MemberOrder;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.services.clock.ClockService;

// TODO: instate when want to ...
//@javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column="VERSION")
public class Event extends EstatioTransactionalObject {

    // {{ StartDate (property)
    private LocalDate startDate;

    @MemberOrder(sequence = "1")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate defaultStartDate() {
        return clockService.now();
    }
    // }}

    // {{ EndDate (property)
    private LocalDate endDate;

    @MemberOrder(sequence = "1")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }
    // }}
    
    // {{ Description (property)
    private String description;

    @MemberOrder(sequence = "1")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
    // }}


    
    // {{ injected: ClockService
    private ClockService clockService;
    public void injectClockService(final ClockService clockService) {
        this.clockService = clockService;
    }
    // }}


}
