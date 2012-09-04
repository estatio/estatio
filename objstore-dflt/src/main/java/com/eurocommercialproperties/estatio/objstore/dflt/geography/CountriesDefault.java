package com.eurocommercialproperties.estatio.objstore.dflt.geography;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.filter.Filter;

import com.eurocommercialproperties.estatio.dom.geography.Countries;
import com.eurocommercialproperties.estatio.dom.geography.Country;

public class CountriesDefault extends AbstractFactoryAndRepository implements Countries {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "countries";
    }

    public String iconName() {
        return "Country";
    }

    // }}

    // {{ NewCountry (hidden)
    @Override
    public Country newCountry(final String reference, String name) {
        final Country country = newTransientInstance(Country.class);
        country.setReference(reference);
        country.setName(name);
        persist(country);
        return country;
    }

    // }}

    // {{ findByReference
    /**
     * Returns the Country with given reference
     */
    @Override
    public Country findByReference(final String reference) {
        return firstMatch(Country.class, new Filter<Country>() {
            @Override
            public boolean accept(final Country country) {
                return reference.equals(country.getReference());
            }
        });
    }

    // }}

    // {{ AllInstances
    @Override
    public List<Country> allInstances() {
        return allInstances(Country.class);
    }
    // }}

}
