package com.eurocommercialproperties.estatio.jdo;

import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActor;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActorType;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActors;
import com.eurocommercialproperties.estatio.dom.party.Party;

import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

public class PropertyActorsJdo extends PropertyActors {

    // {{ findPropertyActor
    @Override
    public PropertyActor findPropertyActor(
            final Property property, 
            final Party party, 
            final PropertyActorType type, 
            final @Named("Start Date") LocalDate startDate, 
            final @Named("End Date") LocalDate endDate) {
        // TODO: need to also search by dates
        return firstMatch(queryForFindByPropertyParty(property, party, type));
    }

    // }}

    // {{ findPropertyActor
    @Override
    public PropertyActor findPropertyActor(final Property property, final Party party, final PropertyActorType type) {
        return firstMatch(queryForFindByPropertyParty(property, party, type));
    }
    
    private static QueryDefault<PropertyActor> queryForFindByPropertyParty(Property property, Party party, PropertyActorType type) {
        return new QueryDefault<PropertyActor>(PropertyActor.class, "properyActor_findByPropertyParty", "property", property, "party", party, "type", type);
    }

}
