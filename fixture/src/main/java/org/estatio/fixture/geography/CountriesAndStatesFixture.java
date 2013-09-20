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
        
        createState("NL-DRN", "Drenthe", countryNED);
        createState("NL-FLE", "Flevoland", countryNED);
        createState("NL-FRI", "Friesland", countryNED);
        createState("NL-GEL", "Gelderland", countryNED);
        createState("NL-GRO", "Groningen", countryNED);
        createState("NL-LIM", "Limburg", countryNED);
        createState("NL-NBT", "Noord-Brabant", countryNED);
        createState("NL-NOH", "Noord-Holland", countryNED);
        createState("NL-OIJ", "Overijssel", countryNED);
        createState("NL-UTR", "Utrecht", countryNED);
        createState("NL-ZEL", "Zeeland", countryNED);
        createState("NL-ZUH", "Zuid-Holland", countryNED);
        
        createState("GB-BED", "Bedfordshire", countryGBR);
        createState("GB-BEK", "Berkshire", countryGBR);
        createState("GB-BUK", "Buckinghamshire", countryGBR);
        createState("GB-CMB", "Cambridgeshire", countryGBR);
        createState("GB-CHE", "Cheshire", countryGBR);
        createState("GB-COR", "Cornwall", countryGBR);
        createState("GB-DBY", "Derbyshire", countryGBR);
        createState("GB-DEV", "Devon", countryGBR);
        createState("GB-DOR", "Dorset", countryGBR);
        createState("GB-DUR", "Durham", countryGBR);
        createState("GB-ESX", "Essex", countryGBR);
        createState("GB-GLO", "Gloucestershire", countryGBR);
        createState("GB-HAN", "Hampshire", countryGBR);
        createState("GB-KNT", "Kent", countryGBR);
        createState("GB-LAN", "Lancashire", countryGBR);
        createState("GB-LEI", "Leicerstershire", countryGBR);
        createState("GB-LIN", "Lincolnshire", countryGBR);
        createState("GB-NFK", "Norfolk", countryGBR);
        createState("GB-NTP", "Northamptonshire", countryGBR);
        createState("GB-NTB", "Northumberland", countryGBR);
        createState("GB-OXF", "Oxfordshire", countryGBR);
        createState("GB-RUT", "Rutland", countryGBR);
        createState("GB-SHR", "Shropshire", countryGBR);
        createState("GB-SOM", "Somerset", countryGBR);
        createState("GB-STA", "Staffordshire", countryGBR);
        createState("GB-SUF", "Suffolk", countryGBR);
        createState("GB-WAR", "Warwickshire", countryGBR);
        createState("GB-WIL", "Wiltshire", countryGBR);
        createState("GB-WOR", "Worcerstershire", countryGBR);
    }

    private Country createCountry(final String reference, String alpha2Code, String name) {
        return countries.createCountry(reference, alpha2Code, name);
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
