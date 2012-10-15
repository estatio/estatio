package com.eurocommercialproperties.estatio.objstore.dflt.asset;

import java.util.List;

import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.PropertyType;
import com.eurocommercialproperties.estatio.dom.communicationchannel.PostalAddress;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.query.QueryDefault;

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
        getContainer().flush();
        persist(property);
        return property;
    }

    // }}

    // {{ FindByReference
    @Override
    @Hidden
    public Property lookupByReference(final String reference) {
        final Query<Property> query = new QueryDefault<Property>(Property.class, "prop_findByReference", "r", reference.toUpperCase()); 
        return this.firstMatch(query);
    }

    // }}

    // {{ FindAllByReference
    @Override
    public List<Property> findPropertiesByReference(final String reference) {
        final Query<Property> query = new QueryDefault<Property>(Property.class, "prop_findByReference", "r", containsArgFor(reference)); 
        return this.allMatches(query);
    }

    private static String containsArgFor(final String reference) {
        return ".*" + reference.toUpperCase() + ".*";
    }

    // }}

    // {{ AllInstances
    @Override
    public List<Property> allProperties() {
        return allInstances(Property.class);
    }
    // }}

    @Override
    @Hidden
    public PostalAddress getPostalAddress(Property prop) {
        return null;
    }

    // {{ autoComplete
    @Hidden
    @Override
    public List<Property> autoComplete(String search) {
        
        final List<Property> findAllByReference = this.findPropertiesByReference(search);
        return findAllByReference;
//        Function<Property, Candidate> function = new Function<Property, Candidate>() {
//
//            @Override
//            public AutoComplete.Candidate apply(Property input) {
//                return new AutoComplete.Candidate(getContainer().titleOf(input), bookmarkFor(input));
//            }
//
//            private Bookmark bookmarkFor(final Object domainObject) {
//                return bookmarkService.bookmarkFor(domainObject);
//            }
//        };
//        return Lists.transform(findAllByReference, function);
    }
    // }}

}
