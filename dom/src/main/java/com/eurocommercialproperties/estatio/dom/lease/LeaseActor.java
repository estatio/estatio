package com.eurocommercialproperties.estatio.dom.lease;

import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.party.Party;

@PersistenceCapable
public class LeaseActor extends AbstractDomainObject {

    // {{ Lease (property)
    private Lease lease;

    @Title(sequence = "1")
    @MemberOrder(sequence = "1")
    public Lease getLease() {
        return lease;
    }

    public void setLease(final Lease lease) {
        this.lease = lease;
    }

    // }}

    // {{ Party (property)
    private Party party;

    @Title(sequence = "2", prepend = " ")
    @MemberOrder(sequence = "2")
    public Party getParty() {
        return party;
    }

    public void setParty(final Party party) {
        this.party = party;
    }

    // }}

    // {{ Type (property)
    private LeaseActorType type;

    @Title(sequence = "3", prepend = " ")
    @MemberOrder(sequence = "3")
    public LeaseActorType getType() {
        return type;
    }

    public void setType(final LeaseActorType type) {
        this.type = type;
    }

    // }}

    // {{ StartDate (property)
    private LocalDate startDate;

    @MemberOrder(sequence = "4")
    @Optional
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    // }}

    // {{ EndDate (property)
    private LocalDate endDate;

    @MemberOrder(sequence = "5")
    @Optional
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }
    // }}

}
