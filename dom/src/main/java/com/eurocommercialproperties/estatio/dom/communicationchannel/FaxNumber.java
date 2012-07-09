package com.eurocommercialproperties.estatio.dom.communicationchannel;

import org.apache.isis.applib.annotation.MemberOrder;

public class FaxNumber extends CommunicationChannel {

    public String title(){
        return "Fax ".concat(getNumber());
    }
    
    // {{ PhoneNumber (property)
    private String number;

    @MemberOrder(sequence = "1")
    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }
    // }}

}
