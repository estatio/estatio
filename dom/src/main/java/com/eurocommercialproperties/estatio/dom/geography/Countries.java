package com.eurocommercialproperties.estatio.dom.geography;

import java.util.List;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.QueryOnly;

public interface Countries {

    @QueryOnly

    @MemberOrder(sequence = "1")
    public Country newCountry(
    		@Named("Reference") String reference,
            @Named("Name") String name);

	List<Country> allInstances();

}
