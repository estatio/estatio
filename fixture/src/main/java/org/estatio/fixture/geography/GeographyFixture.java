package org.estatio.fixture.geography;


import org.apache.isis.applib.fixtures.AbstractFixture;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;
import org.estatio.dom.geography.States;

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
