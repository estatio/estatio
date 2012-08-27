package com.eurocommercialproperties.estatio.fixture.geography;

import com.eurocommercialproperties.estatio.dom.geography.Countries;
import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;
import com.eurocommercialproperties.estatio.dom.geography.States;

import org.apache.isis.applib.fixtures.AbstractFixture;

public class GeographyFixture extends AbstractFixture {

    @Override
    public void install() {
        Country countryGBR = createCountry("GBR", "GB", "United Kingdom");
        Country countryNED = createCountry("NLD", "NL", "Netherlands");
        createState("NL-NH", "Noord-Holland", countryNED);
        createState("GB-OXF", "Oxfordshire", countryGBR);
    }

    private Country createCountry(final String reference, String alpha2Code, String name) {
        Country country = countries.newCountry(reference, name);
        country.setAlpha2Code(alpha2Code);
        return country;
    }

    private State createState(final String reference, String name, Country country) {
        State state = states.newState(reference, name, country);
        return state;
    }

    private States states;

    public void setStateRepository(final States states) {
        this.states = states;
    }

    private Countries countries;

    public void setCountryRepository(final Countries countries) {
        this.countries = countries;
    }

}
