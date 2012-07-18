package com.eurocommercialproperties.estatio.fixture.geography;

import org.apache.isis.applib.fixtures.AbstractFixture;

import com.eurocommercialproperties.estatio.dom.geography.Countries;
import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;
import com.eurocommercialproperties.estatio.dom.geography.States;

public class GeographyFixture extends AbstractFixture {

    @Override
    public void install() {
        Country countryGBR = createCountry("GBR", "United Kingdom");
        Country countryNED = createCountry("NLD", "Netherlands");
        createState("NL-NH", "Noord-Holland", countryNED);
        createState("GB-OXF", "Oxfordshire", countryGBR);
        
        countryNED.setAlpha2Code("NL");
    }

    private Country createCountry(final String reference, String name) {
        Country country = countries.newCountry(reference, name);
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
