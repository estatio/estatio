package com.eurocommercialproperties.estatio.dom.communicationchannel;

import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable(schema="comms")
@javax.jdo.annotations.Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE) // roll-up
@javax.jdo.annotations.Discriminator("FAXN") // required if roll-up
@ObjectType("FAXN")
public class FaxNumber extends CommunicationChannel {

	
    // {{ Number (title, attribute)
    private String number;

    @Title(prepend="Fax ", sequence="1")
    @MemberOrder(sequence = "1")
    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }
    // }}

}
