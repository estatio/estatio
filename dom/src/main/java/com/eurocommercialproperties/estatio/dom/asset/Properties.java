package com.eurocommercialproperties.estatio.dom.asset;

import java.util.List;

import com.eurocommercialproperties.estatio.dom.communicationchannel.PostalAddress;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.query.QueryDefault;

@Named("Properties")
public class Properties extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "properties";
    }
    public String iconName() {
        return "Property";
    }
    // }}

    // {{ NewProperty (action)
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public Property newProperty(
            final @Named("Reference") String reference, 
            final @Named("Name") String name) {
        return newProperty(reference, name, PropertyType.MIXED);
    }
    // }}

    // {{ NewProperty (hidden)
    // for use by fixtures
    @Hidden
    public Property newProperty(
            final String reference, 
            final String name, 
            final PropertyType propertyType) {
        final Property property = newTransientInstance(Property.class);
        property.setReference(reference);
        property.setName(name);
        property.setType(propertyType);
        getContainer().flush();
        persist(property);
        return property;
    }

    // }}

    // {{ findPropertiesByReference, findPropertyByReference (hidden) 
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public List<Property> findPropertiesByReference(
            final @Named("Reference") String reference) {
        return allMatches(queryForFindByReference(reference));
    }

    @Hidden
    public Property findPropertyByReference(
            final String reference) {
        return firstMatch(queryForFindByReference(reference));
    }
    
    private static QueryDefault<Property> queryForFindByReference(String reference) {
        return new QueryDefault<Property>(Property.class, "prop_findByReference", "r", matches(reference));
    }
    private static String matches(final String reference) {
        return ".*" + reference.toUpperCase() + ".*";
    }

    // }}

    // {{ autoComplete (hidden)
    @Hidden
    public List<Property> autoComplete(String searchPhrase) {        
        return findPropertiesByReference(searchPhrase);
    }
    // }}


    // {{ allProperties
    @ActionSemantics(Of.SAFE)
    public List<Property> allProperties() {
        return allInstances(Property.class);
    }
    // }}

}
