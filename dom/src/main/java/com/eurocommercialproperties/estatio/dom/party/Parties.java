package com.eurocommercialproperties.estatio.dom.party;

import java.util.List;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.QueryOnly;

@Named("Parties")
public interface Parties {

    @QueryOnly
    @MemberOrder(sequence = "1")
    public Person newPerson(String initials, String firstName, String lastName);

    @QueryOnly
    @MemberOrder(sequence = "2")
    public Person newOrganisation(String name);

    // public Party findByReference(String reference);

    @MemberOrder(sequence = "3")
    List<Party> allInstances();

}
