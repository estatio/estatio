package com.eurocommercialproperties.estatio.objstore.dflt.asset;

import java.util.List;

import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.PropertyType;

import org.apache.isis.applib.AbstractFactoryAndRepository;

public class PropertiesDefault extends AbstractFactoryAndRepository implements Properties {

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
    @Override
    public Property newProperty(final String reference, String name) {
        return newProperty(reference, name, PropertyType.MIXED);
    }

    // }}

    // {{ NewProperty (hidden)
    @Override
    public Property newProperty(final String reference, String name, PropertyType type) {
        final Property property = newTransientInstance(Property.class);
        property.setReference(reference);
        property.setName(name);
        property.setType(type);
        persist(property);
        return property;
    }

    // }}

    // {{ AllInstances
    @Override
    public List<Property> allInstances() {
        return allInstances(Property.class);
    }
    // }}

}
