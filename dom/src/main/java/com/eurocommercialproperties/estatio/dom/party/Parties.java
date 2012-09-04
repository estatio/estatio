package com.eurocommercialproperties.estatio.dom.party;

import java.util.List;

import org.apache.isis.applib.annotation.Exploration;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.QueryOnly;

@Named("Parties")
public interface Parties {

    @QueryOnly
    @MemberOrder(sequence = "1")
    public Person newPerson(@Named("initials") @Optional String initials, @Named("firstName") @Optional String firstName, @Named("lastName") String lastName);

    @QueryOnly
    @MemberOrder(sequence = "2")
    public Organisation newOrganisation(@Named("name") String name);

    @QueryOnly
    @MemberOrder(sequence = "3")
    public Person findPerson(@Named("firstName") @Optional String firstName, @Named("lastName") @Optional String lastName);

    @QueryOnly
    @MemberOrder(sequence = "4")
    public Organisation findOrganisationByReference(@Named("reference") String reference);

    @QueryOnly
    @MemberOrder(sequence = "5")
    public Organisation findOrganisationByName(@Named("name") String name);

    @QueryOnly
    @MemberOrder(sequence = "6")
    public Party findPartyByReference(@Named("reference") String reference);
    
    //@Exploration
    @QueryOnly
    @MemberOrder(sequence = "7")
    List<Party> allInstances();

}
