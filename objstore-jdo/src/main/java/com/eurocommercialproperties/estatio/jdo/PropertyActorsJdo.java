package com.eurocommercialproperties.estatio.jdo;

import com.eurocommercialproperties.estatio.dom.asset.PropertyActors;

public class PropertyActorsJdo extends PropertyActors {

//    // {{ findPropertyActor
//    public PropertyActor findPropertyActor(
//            final Property property, 
//            final Party party, 
//            final PropertyActorType type, 
//            final @Named("Start Date") LocalDate startDate, 
//            final @Named("End Date") LocalDate endDate) {
////        return firstMatch(PropertyActor.class, new Filter<PropertyActor>() {
////            @Override
////            public boolean accept(final PropertyActor propertyActor) {
////                return propertyActor.getProperty().equals(property) & 
////                        propertyActor.getParty().equals(party) 
////                        //TODO handle optional condition fields as they can contain null
////                        // propertyActor.getStartDate().equals(startDate) & propertyActor.getEndDate().equals(endDate)
////                        ;
////            }
////        });
//        
//        return firstMatch(queryForFindByReference(reference))
//    }
//    // }}
//
//    
//    
//    private static QueryDefault<PropertyActor> queryForFindByReference(String reference) {
//        return new QueryDefault<PropertyActor>(PropertyActor.class, "propact_find", "property", matches(reference));
//    }
//    private static String matches(final String reference) {
//        return ".*" + reference.toUpperCase() + ".*";
//    }

}
