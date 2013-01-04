package org.estatio.jdo;

import org.joda.time.LocalDate;


import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyActor;
import org.estatio.dom.asset.PropertyActorType;
import org.estatio.dom.asset.PropertyActors;
import org.estatio.dom.party.Party;

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
