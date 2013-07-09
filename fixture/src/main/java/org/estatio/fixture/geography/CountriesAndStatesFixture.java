/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.fixture.geography;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;
import org.estatio.dom.geography.States;

public class CountriesAndStatesFixture extends AbstractFixture {

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

    // //////////////////////////////////////

    private States states;

    public void injectsStates(final States states) {
        this.states = states;
    }

    private Countries countries;

    public void injectCountries(final Countries countries) {
        this.countries = countries;
    }

}
