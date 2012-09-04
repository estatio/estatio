package com.eurocommercialproperties.estatio.dom.communicationchannel;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.ObjectType;

@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
@Discriminator("CCHN") // required since subtypes are rolling-up
@ObjectType("CCHN")

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
    
    
    // {{ Reference (property)
    private String reference;

    @Hidden // For import purposes
    public String getReference() {
        return reference;
    }

    public void setReference(final String referencen) {
        this.reference = referencen;
    }
    // }}
}
