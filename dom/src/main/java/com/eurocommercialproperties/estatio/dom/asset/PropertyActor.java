package com.eurocommercialproperties.estatio.dom.asset;

import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.party.Party;

@PersistenceCapable
public class PropertyActor extends AbstractDomainObject {

    // {{ Property (property)
    private Property property;

    @MemberOrder(sequence = "1")
    public Property getProperty() {
        return property;
    }

    public void setProperty(final Property property) {
        this.property = property;
    }

    // }}

    // {{ Party (property)
    private Party party;

    @MemberOrder(sequence = "2")
    public Party getParty() {
        return party;
    }

    public void setParty(final Party party) {
        this.party = party;
    }

    // }}

    // {{ Type (property)
    private PropertyActorType type;

    @MemberOrder(sequence = "3")
    public PropertyActorType getType() {
        return type;
    }

    public void setType(final PropertyActorType type) {
        this.type = type;
    }

    // }}

    // {{ From (property)
    private LocalDate from;

    @MemberOrder(sequence = "4")
    @Optional
    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(final LocalDate from) {
        this.from = from;
    }

    // }}

    // {{ Thru (property)
    private LocalDate thru;

    @MemberOrder(sequence = "5")
    @Optional
    public LocalDate getThru() {
        return thru;
    }

    public void setThru(final LocalDate thru) {
        this.thru = thru;
    }
    // }}

}
