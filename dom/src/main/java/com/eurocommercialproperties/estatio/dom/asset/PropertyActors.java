package com.eurocommercialproperties.estatio.dom.asset;

import org.joda.time.LocalDate;
import java.util.List;

import com.eurocommercialproperties.estatio.dom.party.Party;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.QueryOnly;

@Named("Property Actors")
public interface PropertyActors {

    @QueryOnly
    @MemberOrder(sequence = "1")
    public PropertyActor newPropertyActor(Property property, Party party, PropertyActorType type, LocalDate from, LocalDate thru);

    @QueryOnly
    @MemberOrder(sequence = "1")
    public PropertyActor findPropertyActor(Property property, Party party, PropertyActorType type, LocalDate from, LocalDate thru);

    @QueryOnly
    List<PropertyActor> allInstances();

}
