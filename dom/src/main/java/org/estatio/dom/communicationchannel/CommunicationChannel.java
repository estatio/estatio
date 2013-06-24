package org.estatio.dom.communicationchannel;

import javax.jdo.annotations.DiscriminatorStrategy;
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
import org.estatio.dom.WithNameGetter;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "COMMUNICATIONCHANNEL_ID")
@javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column="VERSION")
@javax.jdo.annotations.Query(name="findByReferenceAndType", language="JDOQL", value="SELECT FROM org.estatio.dom.communicationchannel.CommunicationChannel WHERE (reference == :reference && type == :type)")
@ObjectType("CCHN")
public abstract class CommunicationChannel extends EstatioTransactionalObject<CommunicationChannel> implements /*Comparable<CommunicationChannel>,*/ WithNameGetter {

    public CommunicationChannel() {
        super("type, description");
    }
    
    // //////////////////////////////////////

    private CommunicationChannelType type;

    @Hidden
    public CommunicationChannelType getType() {
        return type;
    }

    public void setType(final CommunicationChannelType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    private String reference;

    @Hidden
    // For import purposes
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    @Title
    @MemberOrder(sequence="1")
    @Hidden(where=Where.OBJECT_FORMS)
    public abstract String getName();

    // //////////////////////////////////////

    private String description;

    @Optional
    @MemberOrder(sequence = "10")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // //////////////////////////////////////

    private boolean legal;

    @MemberOrder(sequence = "10")
    public boolean isLegal() {
        return legal;
    }

    public void setLegal(final boolean Legal) {
        this.legal = Legal;
    }
    

}
