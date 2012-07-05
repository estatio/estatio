package com.eurocommercialproperties.estatio.dom.contactmechanism;

import java.util.List;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.QueryOnly;

import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;

@Named("PostalAddresses")
public interface PostalAddresses {

    @QueryOnly

    @MemberOrder(sequence = "1")
    public PostalAddress newPostalAddress(
    		String address1,
            String address2,
            String city);

    @Hidden // for use by fixtures
    @MemberOrder(sequence = "1")
    public PostalAddress newPostalAddress(
    		String address1,
            String address2,
            String city,
            Country country,
            State state);

	List<PostalAddress> allInstances();

}
