package com.eurocommercialproperties.estatio.objstore.dflt.asset;

import java.util.List;

import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActor;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActorType;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActors;
import com.eurocommercialproperties.estatio.dom.party.Party;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.QueryOnly;
import org.apache.isis.applib.filter.Filter;

import java.util.Date;

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
    @QueryOnly
    @MemberOrder(sequence = "1")
    public PropertyActor newPropertyActor(Property property, Party party, PropertyActorType type, Date from, Date thru) {
        final PropertyActor propertyActor = newTransientInstance(PropertyActor.class);
        propertyActor.setParty(party);
        propertyActor.setProperty(property);
        propertyActor.setFrom(from);
        propertyActor.setThru(thru);
        propertyActor.setType(type);
        persist(propertyActor);
        return propertyActor;
    }

    // }}

    // {{ AllInstances
    @Override
    public List<PropertyActor> allInstances() {
        return allInstances(PropertyActor.class);
    }
    // }}

    @Override
    public PropertyActor findPropertyActor(final Property property, final Party party, PropertyActorType type, final Date from, final Date thru) {
        
        return firstMatch(PropertyActor.class, new Filter<PropertyActor>() {
            @Override
            public boolean accept(final PropertyActor propertyActor) {
                return propertyActor.getProperty().equals(property) & propertyActor.getParty().equals(party) 
                        // propertyActor.getFrom().equals(from) & propertyActor.getThru().equals(thru)
                        ;
            }
        });
    }

}
