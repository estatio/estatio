package com.eurocommercialproperties.estatio.dom.communicationchannel;

import java.util.List;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.QueryOnly;

import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;

@Named("Communication Channels")
public interface CommunicationChannels {

    @QueryOnly
    @MemberOrder(sequence = "1")
    public PostalAddress newPostalAddress(String address1, String address2, String postalCode, String city, State state, Country country);
    
    @MemberOrder(sequence = "2")
    public EmailAddress newEmailAddress(String address);
    
    @MemberOrder(sequence = "3")
    public PhoneNumber newPhoneNumber(String number);

    @MemberOrder(sequence = "4")
    public FaxNumber newFaxNumber(String number);

    
    List<CommunicationChannel> allInstances();

}
