package org.estatio.dom.communicationchannel;

import org.apache.isis.applib.annotation.Hidden;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;

@Hidden
public class CommunicationChannels extends EstatioDomainService<CommunicationChannel> {

    public CommunicationChannels() {
        super(CommunicationChannels.class, CommunicationChannel.class);
    }
    
    // //////////////////////////////////////

    @Hidden
    public PostalAddress newPostalAddress(final String address1, final String address2, final String postalCode, final String city, final State state, final Country country) {
        final PostalAddress pa = newTransientInstance(PostalAddress.class);
        pa.setType(CommunicationChannelType.POSTAL_ADDRESS);
        pa.setAddress1(address1);
        pa.setAddress2(address2);
        pa.setCity(city);
        pa.setPostalCode(postalCode);
        pa.setState(state);
        pa.setCountry(country);
        persist(pa);
        return pa;
    }

    @Hidden
    public EmailAddress newEmailAddress(final String address) {
        final EmailAddress ea = newTransientInstance(EmailAddress.class);
        ea.setType(CommunicationChannelType.EMAIL_ADDRESS);
        ea.setAddress(address);
        persist(ea);
        return ea;
    }

    @Hidden
    public PhoneNumber newPhoneNumber(final String number) {
        final PhoneNumber pn = newTransientInstance(PhoneNumber.class);
        pn.setType(CommunicationChannelType.PHONE_NUMBER);
        pn.setPhoneNumber(number);
        persist(pn);
        return pn;
    }

    @Hidden
    public FaxNumber newFaxNumber(final String number) {
        final FaxNumber fn = newTransientInstance(FaxNumber.class);
        fn.setType(CommunicationChannelType.FAX_NUMBER);
        fn.setFaxNumber(number);
        persist(fn);
        return fn;
    }


}
