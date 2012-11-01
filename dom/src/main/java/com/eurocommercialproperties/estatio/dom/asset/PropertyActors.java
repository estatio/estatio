package com.eurocommercialproperties.estatio.dom.asset;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.filter.Filter;

import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.party.Party;

@Hidden
@Named("Property Actors")
public class PropertyActors extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "propertyActors";
    }

    public String iconName() {
        return "PropertActor";
    }

    // }}

    // {{ NewProperty
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public PropertyActor newPropertyActor(
            final Property property, 
            final Party party, 
            final PropertyActorType type, 
            final @Named("Start Date") LocalDate startDate, 
            final @Named("End Date") LocalDate endDate) {
        final PropertyActor propertyActor = newTransientInstance(PropertyActor.class);
        propertyActor.setParty(party);
        propertyActor.setProperty(property);
        propertyActor.setStartDate(startDate);
        propertyActor.setEndDate(endDate);
        propertyActor.setType(type);
        persist(propertyActor);
        return propertyActor;
    }
    // }}

    // {{ findPropertyActor
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public PropertyActor findPropertyActor(
            final Property property, 
            final Party party, 
            final PropertyActorType type, 
            final @Named("Start Date") LocalDate startDate, 
            final @Named("End Date") LocalDate endDate) {
        return firstMatch(PropertyActor.class, new Filter<PropertyActor>() {
            @Override
            public boolean accept(final PropertyActor propertyActor) {
                return propertyActor.getProperty().equals(property) & propertyActor.getParty().equals(party) 
                        //TODO handle optional condition fields as they can contain null
                        // propertyActor.getStartDate().equals(startDate) & propertyActor.getEndDate().equals(endDate)
                        ;
            }
        });
    }
    // }}

    // {{ AllInstances
    @ActionSemantics(Of.SAFE)
    public List<PropertyActor> allInstances() {
        return allInstances(PropertyActor.class);
    }
    // }}


}
