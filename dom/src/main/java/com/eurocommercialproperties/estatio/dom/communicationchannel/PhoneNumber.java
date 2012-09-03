package com.eurocommercialproperties.estatio.dom.communicationchannel;

import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

@PersistenceCapable
public class PhoneNumber extends CommunicationChannel {

    // {{ Number (attribute)
    private String number;

    @Title(sequence = "1", prepend = "Phone ")
    @MemberOrder(sequence = "1")
    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }
    // }}

}
