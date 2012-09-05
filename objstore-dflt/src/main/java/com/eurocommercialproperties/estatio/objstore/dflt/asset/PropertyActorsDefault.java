package com.eurocommercialproperties.estatio.objstore.dflt.asset;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.filter.Filter;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActor;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActorType;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActors;
import com.eurocommercialproperties.estatio.dom.party.Party;

public class PropertyActorsDefault extends AbstractFactoryAndRepository implements PropertyActors {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "propertyActors";
    }

    public String iconName() {
        return "PropertActor";
    }

    // }}

    // {{ NewProperty (hidden)
    @Override
    @MemberOrder(sequence = "1")
    public PropertyActor newPropertyActor(Property property, Party party, PropertyActorType type, LocalDate startDate, LocalDate endDate) {
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

    // {{ AllInstances
    @Override
    @ActionSemantics(Of.SAFE)
    public List<PropertyActor> allInstances() {
        return allInstances(PropertyActor.class);
    }
    // }}

    @Override
    @ActionSemantics(Of.SAFE)
    public PropertyActor findPropertyActor(final Property property, final Party party, PropertyActorType type, final LocalDate startDate, final LocalDate endDate) {
        
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

}
