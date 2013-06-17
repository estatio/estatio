package org.estatio.dom.asset;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;

@Named("Properties")
public class Properties extends EstatioDomainService {

    public Properties() {
        super(Properties.class, Property.class);
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public Property newProperty(final @Named("Reference") String reference, final @Named("Name") String name) {
        return newProperty(reference, name, PropertyType.MIXED);
    }

    @Hidden
    public Property newProperty(final String reference, final String name, final PropertyType propertyType) {
        final Property property = newTransientInstance(Property.class);
        property.setReference(reference);
        property.setName(name);
        property.setPropertyType(propertyType);
        persistIfNotAlready(property);
        return property;
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public List<Property> findPropertiesByReference(final @Named("Reference") String reference) {
        throw new NotImplementedException();
    }

    @Hidden
    public Property findPropertyByReference(final String reference) {
        throw new NotImplementedException();
    }

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<Property> allProperties() {
        return allInstances(Property.class);
    }

    @Hidden
    public List<Property> autoComplete(String searchPhrase) {
        return findPropertiesByReference("*".concat(searchPhrase).concat("*"));
    }
}
