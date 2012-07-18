package com.eurocommercialproperties.estatio.dom.communicationchannel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Hidden;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy=IdGeneratorStrategy.IDENTITY)
@javax.jdo.annotations.Discriminator("CCHN")
@javax.jdo.annotations.Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
public abstract class CommunicationChannel extends AbstractDomainObject {

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

}
