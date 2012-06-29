package com.eurocommercialproperties.estatio.dom.party;

import java.util.List;

import org.apache.isis.applib.annotation.Exploration;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.QueryOnly;

@Named("Owners")
public interface Owners {

    @QueryOnly
    @MemberOrder(sequence = "1")
    public Owner newOwner(
            @Named("Name") String name);

    // @Exploration // TODO: would be nice to associate with Wicket's DEVELOPMENT mode
	List<Owner> allInstances();

}
