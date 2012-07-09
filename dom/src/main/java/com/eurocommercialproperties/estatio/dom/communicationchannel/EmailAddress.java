package com.eurocommercialproperties.estatio.dom.communicationchannel;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

public class EmailAddress extends CommunicationChannel {

    // {{ EmailAddress (property)
    private String address;

    @Title
    @MemberOrder(sequence = "1")
    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }
    // }}

}