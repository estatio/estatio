package com.eurocommercialproperties.estatio.dom.lease;

import org.joda.time.LocalDate;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import com.eurocommercialproperties.estatio.dom.party.Party;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Optional;

@javax.jdo.annotations.PersistenceCapable(schema = "lease", identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator("LEAC")
// required since subtypes are rolling-up
@ObjectType("LEAC")
public class LeaseActor extends AbstractDomainObject {

    // {{ Lease (property)
    private Lease lease;

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

    @MemberOrder(sequence = "3")
    public LeaseActorType getType() {
        return type;
    }

    public void setType(final LeaseActorType type) {
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
