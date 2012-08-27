package com.eurocommercialproperties.estatio.dom.asset;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;

import java.util.Date;

import com.eurocommercialproperties.estatio.dom.party.Party;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.runtimes.dflt.objectstores.jdo.applib.annotations.Auditable;

@javax.jdo.annotations.PersistenceCapable(schema = "asset", identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator("PRAC")
// required since subtypes are rolling-up
@ObjectType("PRAC")
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
    private Date from;

    @MemberOrder(sequence = "4")
    @Optional
    public Date getFrom() {
        return from;
    }

    public void setFrom(final Date from) {
        this.from = from;
    }

    // }}
    
    // {{ Thru (property)
    private Date thru;

    @MemberOrder(sequence = "5")
    @Optional
    public Date getThru() {
        return thru;
    }

    public void setThru(final Date thru) {
        this.thru = thru;
    }
    // }}

}
