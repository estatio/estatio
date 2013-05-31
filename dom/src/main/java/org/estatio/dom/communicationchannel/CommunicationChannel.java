package org.estatio.dom.communicationchannel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator("CCHN") // required since subtypes are rolling-up
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "COMMUNICATIONCHANNEL_ID")
@javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column="VERSION")
@ObjectType("CCHN")
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
    public boolean isLegal() {
        return Legal;
    }

    public void setLegal(final boolean Legal) {
        this.Legal = Legal;
    }
    // }}
    

    // {{ Comparable impl
    @Hidden
    public int compareTo(CommunicationChannel other) {
        return CommunicationChannelType.ORDERING_BY_TYPE.compare(this.getType(), other.getType());
    }
    // }}

}
