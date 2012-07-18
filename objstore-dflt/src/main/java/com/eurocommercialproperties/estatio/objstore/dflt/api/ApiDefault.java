package com.eurocommercialproperties.estatio.objstore.dflt.api;

import com.eurocommercialproperties.estatio.api.Api;
import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.PropertyType;
import com.eurocommercialproperties.estatio.dom.asset.Units;
import com.eurocommercialproperties.estatio.dom.geography.Countries;
import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;
import com.eurocommercialproperties.estatio.dom.geography.States;
import com.eurocommercialproperties.estatio.dom.party.Owner;
import com.eurocommercialproperties.estatio.dom.party.Owners;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.value.Date;

public class ApiDefault extends AbstractFactoryAndRepository implements Api {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "api";
    }

    public String iconName() {
        return "Api";
    }

    // }}

    // TODO: would like to add a meaningful prefix to the methods but set (as in
    // setCountry) is ignored and createOrUpdate (as in
    // createOrUpdatePropertyPostalAddress) creates long method names.
    // Suggestions will be rewarded with a beer.
    //
    // SUGGESTION: how about putCountry?  (cf HTTP PUT, it's an idempotent action).  Or maybe uploadCountry() ?

    @Override
    public void country(String code, String alpha2Code, String name) {
        Country country = countries.findByReference(code);
        if (country == null) {
            country = countries.newCountry(code, name);
        }
        country.setName(name);
        country.setAlpha2Code(alpha2Code);
    }

    @Override
    public void state(String code, String name, String countryCode) {
        Country country = countries.findByReference(countryCode);
        if (country == null) {
            throw new ApplicationException(String.format("Country with code %1$s not found", countryCode));
        }
        State state = states.findByReference(countryCode);
        if (state == null) {
            state = states.newState(code, name, country);
        }
        state.setName(name);
        state.setCountry(country);
    }

    @Override
    public void owner(String reference, String name) {
        Owner owner = owners.findByReference(reference);
        if (owner == null) {
            owner = owners.newOwner(reference, name);
        }
        owner.setName(name);
    }

    @Override
    public void propertyPostalAddress(String propertyReference, String address1, String address2, String postalCode,
                    String stateCode, String countryCode) {
        // TODO Auto-generated method stub
    }

    @Override
    public void propertyOwner(String reference, String ownerReference) {
        // TODO Auto-generated method stub
    }

    @Override
    public void property(String reference, String name, String type, Date acquireDate, Date disposalDate,
                    Date openingDate, String ownerReference) {
        Owner owner = owners.findByReference(ownerReference);
        if (owner == null) {
            throw new ApplicationException(String.format("Owner with reference %s not found.", ownerReference));
        }
        Property property = properties.findByReference(reference);
        if (property == null) {
            property = properties.newProperty(reference, name);
        }
        property.setName(name);
        property.setType(PropertyType.valueOf(type));
        property.setAcquireDate(acquireDate);
        property.setDisposalDate(disposalDate);
        property.setOpeningDate(openingDate);
        property.addOwner(owner);
    }

    private Countries countries;

    public void setCountryRepository(final Countries countries) {
        this.countries = countries;
    }

    private States states;

    public void setStateRepository(final States states) {
        this.states = states;
    }

    private Units units;

    public void setUnitRepository(final Units units) {
        this.units = units;
    }

    private Properties properties;

    public void setPropertyRepository(final Properties properties) {
        this.properties = properties;
    }

    private Owners owners;

    public void setOwnerRepository(final Owners owners) {
        this.owners = owners;
    }

}
