package com.eurocommercialproperties.estatio.dom.party;

import java.util.List;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.QueryOnly;

@Named("Owners")
public interface Owners {

    @QueryOnly
    @MemberOrder(sequence = "1")
    public Owner newOwner(String reference, @Named("Name") String name);
    
    @QueryOnly
    @MemberOrder(sequence = "2")
    public Owner findByReference(String reference);
    
    // @Exploration // TODO: would be nice to associate with Wicket's
    // DEVELOPMENT mode
    @MemberOrder(sequence = "3")
    List<Owner> allInstances();

}
