package com.eurocommercialproperties.estatio.dom.asset;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.party.Party;

@Hidden
@Named("Property Actors")
public interface PropertyActors {

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public PropertyActor newPropertyActor(Property property, Party party, PropertyActorType type, LocalDate startDate, LocalDate endDate);

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public PropertyActor findPropertyActor(Property property, Party party, PropertyActorType type, LocalDate startDate, LocalDate endDate);

    @ActionSemantics(Of.SAFE)
    List<PropertyActor> allInstances();

}
