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
import org.estatio.fixture.EstatioFixtureScript;

public class StatesRefData extends EstatioFixtureScript {

    public static final String GBR = "GBR";
    public static final String NLD = "NLD";
    public static final String ITA = "ITA";
    public static final String FRA = "FRA";
    public static final String SWE = "SWE";

    public static final String GB = "GB";
    public static final String IT = "IT";
    public static final String NL = "NL";
    public static final String SE = "SE";
    public static final String FR = "FR";

    @Override
    protected void execute(ExecutionContext executionContext) {

        Country countryGBR = countries.findCountry(GBR);
        Country countryNED = countries.findCountry(NLD);
        Country countryITA = countries.findCountry(ITA);
        Country countryFRA = countries.findCountry(FRA);
        Country countrySWE = countries.findCountry(SWE);
        
        createState(countryNED, "-DRN", "Drenthe", executionContext);
        createState(countryNED, "-FLE", "Flevoland", executionContext);
        createState(countryNED, "-FRI", "Friesland", executionContext);
        createState(countryNED, "-GEL", "Gelderland", executionContext);
        createState(countryNED, "-GRO", "Groningen", executionContext);
        createState(countryNED, "-LIM", "Limburg", executionContext);
        createState(countryNED, "-NBT", "Noord-Brabant", executionContext);
        createState(countryNED, "-NOH", "Noord-Holland", executionContext);
        createState(countryNED, "-OIJ", "Overijssel", executionContext);
        createState(countryNED, "-UTR", "Utrecht", executionContext);
        createState(countryNED, "-ZEL", "Zeeland", executionContext);
        createState(countryNED, "-ZUH", "Zuid-Holland", executionContext);

        createState(countryGBR, "-BED", "Bedfordshire", executionContext);
        createState(countryGBR, "-BEK", "Berkshire", executionContext);
        createState(countryGBR, "-BUK", "Buckinghamshire", executionContext);
        createState(countryGBR, "-CMB", "Cambridgeshire", executionContext);
        createState(countryGBR, "-CHE", "Cheshire", executionContext);
        createState(countryGBR, "-COR", "Cornwall", executionContext);
        createState(countryGBR, "-DBY", "Derbyshire", executionContext);
        createState(countryGBR, "-DEV", "Devon", executionContext);
        createState(countryGBR, "-DOR", "Dorset", executionContext);
        createState(countryGBR, "-DUR", "Durham", executionContext);
        createState(countryGBR, "-ESX", "Essex", executionContext);
        createState(countryGBR, "-GLO", "Gloucestershire", executionContext);
        createState(countryGBR, "-HAN", "Hampshire", executionContext);
        createState(countryGBR, "-KNT", "Kent", executionContext);
        createState(countryGBR, "-LAN", "Lancashire", executionContext);
        createState(countryGBR, "-LEI", "Leicerstershire", executionContext);
        createState(countryGBR, "-LIN", "Lincolnshire", executionContext);
        createState(countryGBR, "-NFK", "Norfolk", executionContext);
        createState(countryGBR, "-NTP", "Northamptonshire", executionContext);
        createState(countryGBR, "-NTB", "Northumberland", executionContext);
        createState(countryGBR, "-OXF", "Oxfordshire", executionContext);
        createState(countryGBR, "-RUT", "Rutland", executionContext);
        createState(countryGBR, "-SHR", "Shropshire", executionContext);
        createState(countryGBR, "-SOM", "Somerset", executionContext);
        createState(countryGBR, "-STA", "Staffordshire", executionContext);
        createState(countryGBR, "-SUF", "Suffolk", executionContext);
        createState(countryGBR, "-WAR", "Warwickshire", executionContext);
        createState(countryGBR, "-WIL", "Wiltshire", executionContext);
        createState(countryGBR, "-WOR", "Worcerstershire", executionContext);
    }

    private State createState(Country country, final String referenceSuffix, String name, ExecutionContext executionContext) {
        final String reference = country.getAlpha2Code() + referenceSuffix;
        final State state = states.newState(reference, name, country);
        return executionContext.addResult(this, state.getReference(), state);
    }

    // //////////////////////////////////////

    @Inject
    private States states;

    @Inject
    private Countries countries;

}
