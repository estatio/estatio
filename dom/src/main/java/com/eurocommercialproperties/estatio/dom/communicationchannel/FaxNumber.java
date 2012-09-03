package com.eurocommercialproperties.estatio.dom.communicationchannel;

import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

@PersistenceCapable
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
