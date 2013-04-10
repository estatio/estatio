package org.estatio.dom.communicationchannel;

import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.VersionStrategy;

import org.estatio.dom.EstatioTransactionalObject;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Discriminator("CCHN")
// required since subtypes are rolling-up
@ObjectType("CCHN")
@DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "COMMUNICATIONCHANNEL_ID")
@javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column="VERSION")
public abstract class CommunicationChannel extends EstatioTransactionalObject implements Comparable<CommunicationChannel> {

    // {{ Type (attribute)
    private CommunicationChannelType type;

    @Hidden
    public CommunicationChannelType getType() {
        return type;
    }

    public void setType(final CommunicationChannelType type) {
        this.type = type;
    }

    // }}

    // {{ Reference (property)
    private String reference;

    @Hidden
    // For import purposes
    public String getReference() {
        return reference;
    }

    public void setReference(final String referencen) {
        this.reference = referencen;
    }

    // }}

    @Title
    @MemberOrder(sequence="1")
    @Hidden(where=Where.OBJECT_FORMS)
    public abstract String getName();

    // {{ Description (property)
    private String description;

    @Optional
    @MemberOrder(sequence = "10")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // }}

    // {{ Legal (property)
    private boolean Legal;

    @MemberOrder(sequence = "10")
    public boolean getLegal() {
        return Legal;
    }

    public void setLegal(final boolean Legal) {
        this.Legal = Legal;
    }
    // }}
    
    
    @Hidden
    public int compareTo(CommunicationChannel other) {
        return this.getClass().getName().compareTo(other.getClass().getName());
    }

}
