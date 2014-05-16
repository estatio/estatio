/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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

import javax.inject.Inject;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;
import org.estatio.dom.geography.States;
import org.apache.isis.applib.fixturescripts.SimpleFixtureScript;

public class CountriesAndStatesFixture extends SimpleFixtureScript {

    @Override
    protected void execute(ExecutionContext fixtureResults) {

        Country countryGBR = createCountry("GBR", "GB", "United Kingdom", fixtureResults);
        Country countryNED = createCountry("NLD", "NL", "Netherlands", fixtureResults);
        
        createState("NL-DRN", "Drenthe", countryNED, fixtureResults);
        createState("NL-FLE", "Flevoland", countryNED, fixtureResults);
        createState("NL-FRI", "Friesland", countryNED, fixtureResults);
        createState("NL-GEL", "Gelderland", countryNED, fixtureResults);
        createState("NL-GRO", "Groningen", countryNED, fixtureResults);
        createState("NL-LIM", "Limburg", countryNED, fixtureResults);
        createState("NL-NBT", "Noord-Brabant", countryNED, fixtureResults);
        createState("NL-NOH", "Noord-Holland", countryNED, fixtureResults);
        createState("NL-OIJ", "Overijssel", countryNED, fixtureResults);
        createState("NL-UTR", "Utrecht", countryNED, fixtureResults);
        createState("NL-ZEL", "Zeeland", countryNED, fixtureResults);
        createState("NL-ZUH", "Zuid-Holland", countryNED, fixtureResults);
        
        createState("GB-BED", "Bedfordshire", countryGBR, fixtureResults);
        createState("GB-BEK", "Berkshire", countryGBR, fixtureResults);
        createState("GB-BUK", "Buckinghamshire", countryGBR, fixtureResults);
        createState("GB-CMB", "Cambridgeshire", countryGBR, fixtureResults);
        createState("GB-CHE", "Cheshire", countryGBR, fixtureResults);
        createState("GB-COR", "Cornwall", countryGBR, fixtureResults);
        createState("GB-DBY", "Derbyshire", countryGBR, fixtureResults);
        createState("GB-DEV", "Devon", countryGBR, fixtureResults);
        createState("GB-DOR", "Dorset", countryGBR, fixtureResults);
        createState("GB-DUR", "Durham", countryGBR, fixtureResults);
        createState("GB-ESX", "Essex", countryGBR, fixtureResults);
        createState("GB-GLO", "Gloucestershire", countryGBR, fixtureResults);
        createState("GB-HAN", "Hampshire", countryGBR, fixtureResults);
        createState("GB-KNT", "Kent", countryGBR, fixtureResults);
        createState("GB-LAN", "Lancashire", countryGBR, fixtureResults);
        createState("GB-LEI", "Leicerstershire", countryGBR, fixtureResults);
        createState("GB-LIN", "Lincolnshire", countryGBR, fixtureResults);
        createState("GB-NFK", "Norfolk", countryGBR, fixtureResults);
        createState("GB-NTP", "Northamptonshire", countryGBR, fixtureResults);
        createState("GB-NTB", "Northumberland", countryGBR, fixtureResults);
        createState("GB-OXF", "Oxfordshire", countryGBR, fixtureResults);
        createState("GB-RUT", "Rutland", countryGBR, fixtureResults);
        createState("GB-SHR", "Shropshire", countryGBR, fixtureResults);
        createState("GB-SOM", "Somerset", countryGBR, fixtureResults);
        createState("GB-STA", "Staffordshire", countryGBR, fixtureResults);
        createState("GB-SUF", "Suffolk", countryGBR, fixtureResults);
        createState("GB-WAR", "Warwickshire", countryGBR, fixtureResults);
        createState("GB-WIL", "Wiltshire", countryGBR, fixtureResults);
        createState("GB-WOR", "Worcerstershire", countryGBR, fixtureResults);
    }

    private Country createCountry(final String reference, String alpha2Code, String name, ExecutionContext executionContext) {
        final Country country = countries.createCountry(reference, alpha2Code, name);
        return executionContext.add(this, country.getAlpha2Code(), country);
    }

    private State createState(final String reference, String name, Country country, ExecutionContext executionContext) {
        final State state = states.newState(reference, name, country);
        return executionContext.add(this, state.getReference(), state);
    }

    // //////////////////////////////////////

    @Inject
    private States states;

    @Inject
    private Countries countries;

}
