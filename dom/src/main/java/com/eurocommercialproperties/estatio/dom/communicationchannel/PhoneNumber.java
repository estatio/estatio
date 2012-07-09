package com.eurocommercialproperties.estatio.dom.communicationchannel;

import org.apache.isis.applib.annotation.MemberOrder;

public class PhoneNumber extends CommunicationChannel {
    
    public String title(){
        return "Phone ".concat(getNumber());
    }
    

    // {{ PhoneNumber (property)
    private String number;

    //TODO: @Title annotation doesn't seem to work 
    //@Title(sequence = "1", prepend = "Phone ")
    @MemberOrder(sequence = "1")
    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }
    // }}

}
