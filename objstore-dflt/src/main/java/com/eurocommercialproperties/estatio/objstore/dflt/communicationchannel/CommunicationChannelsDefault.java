package com.eurocommercialproperties.estatio.objstore.dflt.communicationchannel;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.QueryOnly;

import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannel;
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannels;
import com.eurocommercialproperties.estatio.dom.communicationchannel.EmailAddress;
import com.eurocommercialproperties.estatio.dom.communicationchannel.FaxNumber;
import com.eurocommercialproperties.estatio.dom.communicationchannel.PhoneNumber;
import com.eurocommercialproperties.estatio.dom.communicationchannel.PostalAddress;
import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;

public class CommunicationChannelsDefault extends AbstractFactoryAndRepository implements CommunicationChannels {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "communicationchannels";
    }

    public String iconName() {
        return "CommunicationChannel";
    }

    // }}

    // {{ AllInstances
    @Override
    public List<CommunicationChannel> allInstances() {
        return allInstances(CommunicationChannel.class);
    }

    // }}

    @Override
    public List<PostalAddress> allPostalAddresses() {
        return allInstances(PostalAddress.class);
    }

    // }}

    @Override
     @MemberOrder(sequence = "1")
    public PostalAddress newPostalAddress(String address1, String address2, String postalCode, String city,
                    State state, Country country) {
        final PostalAddress pa = newTransientInstance(PostalAddress.class);
        pa.setAddress1(address1);
        pa.setAddress2(address2);
        pa.setCity(city);
        pa.setPostalCode(postalCode);
        pa.setState(state);
        pa.setCountry(country);
        persist(pa);
        return pa;
    }

    @Override
    @MemberOrder(sequence = "2")
    public EmailAddress newEmailAddress(String address) {
        final EmailAddress ea = newTransientInstance(EmailAddress.class);
        ea.setAddress(address);
        persist(ea);
        return ea;
    }

    @Override
    @MemberOrder(sequence = "3")
    public PhoneNumber newPhoneNumber(String number) {
        final PhoneNumber pn = newTransientInstance(PhoneNumber.class);
        pn.setPhoneNumber(number);
        persist(pn);
        return pn;
    }

    @Override
    @MemberOrder(sequence = "4")
    public FaxNumber newFaxNumber(String number) {
        final FaxNumber fn = newTransientInstance(FaxNumber.class);
        fn.setFaxNumber(number);
        persist(fn);
        return fn;
    }

}
