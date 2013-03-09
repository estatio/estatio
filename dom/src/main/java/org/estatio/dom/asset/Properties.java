package org.estatio.dom.asset;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

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
        throw new NotImplementedException();
    }

    @Hidden
    public Property findPropertyByReference(
            final String reference) {
        throw new NotImplementedException();
    }

    // }}

    // {{ autoComplete (hidden)
    @Hidden
    public List<Property> autoComplete(String searchPhrase) {        
        return findPropertiesByReference(searchPhrase);
    }
    // }}


    // {{ allProperties
    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<Property> allProperties() {
        
        List<Property> allInstances = allInstances(Property.class);
        if(allInstances.isEmpty()) {
            warnUser("oh no!");
        } else {
            informUser("woohoo!");
        }
        return allInstances;
    }
    // }}

}
