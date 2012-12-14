package com.eurocommercialproperties.estatio.dom.communicationchannel;

import java.util.List;

import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

@Named("Communication Channels")
public class CommunicationChannels extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "communicationchannels";
    }

    public String iconName() {
        return "CommunicationChannel";
    }

    // }}
    
    // {{ newPostalAddress
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public PostalAddress newPostalAddress(
            final @Named("Address 1") String address1, 
            final @Named("Address 2") String address2, 
            final @Named("Postal Code") String postalCode, 
            final @Named("City") String city,
            final State state, 
            final Country country) {
        final PostalAddress pa = newTransientInstance(PostalAddress.class);
        pa.setType(CommunicationChannelType.LEGAL_POSTAL_ADDRESS);
        pa.setAddress1(address1);
        pa.setAddress2(address2);
        pa.setCity(city);
        pa.setPostalCode(postalCode);
        pa.setState(state);
        pa.setCountry(country);
        persist(pa);
        return pa;
    }
    // }}

    // {{ newEmailAddress
    @MemberOrder(sequence = "2")
    public EmailAddress newEmailAddress(
            final @Named("Address") String address) {
        final EmailAddress ea = newTransientInstance(EmailAddress.class);
        ea.setType(CommunicationChannelType.LEGAL_EMAIL_ADDRESS);
        ea.setAddress(address);
        persist(ea);
        return ea;
    }
    // }}

    // {{ newPhoneNumber
    @MemberOrder(sequence = "3")
    public PhoneNumber newPhoneNumber(
            final @Named("Number") String number) {
        final PhoneNumber pn = newTransientInstance(PhoneNumber.class);
        pn.setType(CommunicationChannelType.PHONE_NUMBER);
        pn.setPhoneNumber(number);
        persist(pn);
        return pn;
    }
    // }}
 
    // {{ newFaxNumber
    @MemberOrder(sequence = "4")
    public FaxNumber newFaxNumber(
            final @Named("Number") String number) {
        final FaxNumber fn = newTransientInstance(FaxNumber.class);
        fn.setType(CommunicationChannelType.FAX_NUMBER);
        fn.setFaxNumber(number);
        persist(fn);
        return fn;
    }
    // }}

    // {{ AllCommunicationChannel
    @Prototype
    public List<CommunicationChannel> allCommunicationChannel() {
        return allInstances(CommunicationChannel.class);
    }
    // }}

    // {{ allPostalAddresses
    @Prototype
    public List<PostalAddress> allPostalAddresses() {
        return allInstances(PostalAddress.class);
    }
    // }}


}
