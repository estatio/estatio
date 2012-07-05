package com.eurocommercialproperties.estatio.dom.geography;

import java.util.List;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.QueryOnly;

public interface States {

    @QueryOnly

    @MemberOrder(sequence = "1")
    public State newState(
    		@Named("Reference") String reference,
            @Named("Name") String name,
            Country country);

	List<State> allInstances();

}
