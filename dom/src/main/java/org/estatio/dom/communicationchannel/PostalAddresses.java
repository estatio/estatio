package org.estatio.dom.communicationchannel;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.geography.Country;

import org.apache.isis.applib.annotation.Hidden;

@Hidden
public class PostalAddresses extends EstatioDomainService<PostalAddress> {

    public PostalAddresses() {
        super(PostalAddresses.class, PostalAddress.class);
    }

    // //////////////////////////////////////

    public CommunicationChannel findByAddress(String address1, String postalCode, String city, Country country) {
        return firstMatch("findByAddress", "address1", address1, "postalCode", postalCode, "city", city, "country", country);
    }

}
