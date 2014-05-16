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
package org.estatio.fixture.asset;

import java.math.BigDecimal;
import javax.inject.Inject;
import org.estatio.dom.asset.*;
import org.estatio.dom.communicationchannel.CommunicationChannelContributions;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;
import org.estatio.dom.geography.States;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;
import org.apache.isis.applib.fixturescripts.SimpleFixtureScript;

public class PropertiesAndUnitsFixture extends SimpleFixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        Party owner1 = parties.findPartyByReference("HELLOWORLD");
        Party owner2 = parties.findPartyByReference("ACME");
        Party manager = parties.findPartyByReference("JDOE");

        State state = states.findState("GB-OXF");
        Country country = countries.findCountry("GBR");

        Property prop1 = createPropertyAndUnits("OXF", "Oxford Super Mall", "Oxford", country, PropertyType.SHOPPING_CENTER, 25, new LocalDate(1999, 1, 1), new LocalDate(2008, 6, 1), owner1, manager, 51.74579, -1.24334, executionContext);

        communicationChannelContributedActions.newPostal(prop1, CommunicationChannelType.POSTAL_ADDRESS, country, state, "1 Market Street", null, null, "OX1 3HL", "Oxford");
        communicationChannelContributedActions.newPhoneOrFax(prop1, CommunicationChannelType.PHONE_NUMBER, "+44 123 456789");
        communicationChannelContributedActions.newPhoneOrFax(prop1, CommunicationChannelType.FAX_NUMBER, "+44 987 654321");
        communicationChannelContributedActions.newEmail(prop1, CommunicationChannelType.EMAIL_ADDRESS, "info@oxford.example.com");


        Property prop2 = createPropertyAndUnits("KAL", "Kalvertoren", "Amsterdam", country, PropertyType.SHOPPING_CENTER, 40, new LocalDate(2003, 12, 1), new LocalDate(2003, 12, 1), owner2, manager, 52.37597, 4.90814, executionContext);
        Country c2 = countries.findCountry("NLD");
        State s2 = states.findState("NL-NH");

        communicationChannelContributedActions.newPostal(prop2, CommunicationChannelType.POSTAL_ADDRESS, c2, s2, "Kalverstraat 12", null, null, "1017 AA", "Amsterdam");
        communicationChannelContributedActions.newPhoneOrFax(prop2, CommunicationChannelType.PHONE_NUMBER, "+31 123 456789");
        communicationChannelContributedActions.newPhoneOrFax(prop2, CommunicationChannelType.FAX_NUMBER, "+31 987 654321");
        communicationChannelContributedActions.newEmail(prop2, CommunicationChannelType.EMAIL_ADDRESS, "info@kalvertoren.example.com");
    }

    private Property createPropertyAndUnits(final String reference, String name, String city, Country country, PropertyType type, int numberOfUnits, LocalDate openingDate, LocalDate acquireDate, Party owner, Party manager, double lat, double lng, ExecutionContext fixtureResults) {
        Property property = properties.newProperty(reference, name, type, city, country, acquireDate);
        property.setOpeningDate(openingDate);
        property.addRoleIfDoesNotExist(owner, FixedAssetRoleType.PROPERTY_OWNER, new LocalDate(1999, 1, 1), new LocalDate(2000, 1, 1));
        property.addRoleIfDoesNotExist(manager, FixedAssetRoleType.ASSET_MANAGER, null, null);
        for (int i = 0; i < numberOfUnits; i++) {
            int unitNumber = i + 1;
            property.newUnit(String.format("%s-%03d", reference, unitNumber), "Unit " + unitNumber, unitType(i)).setArea(new BigDecimal((i + 1) * 100));
        }
        return fixtureResults.add(this, property.getReference(), property);
    }

    private UnitType unitType(int n) {
        final UnitType[] unitTypes = UnitType.values();
        return unitTypes[n % unitTypes.length];
    }

    // //////////////////////////////////////

    @Inject
    private States states;

    @Inject
    private Countries countries;

    @Inject
    private Properties properties;

    @Inject
    private Parties parties;

    @Inject
    private CommunicationChannelContributions communicationChannelContributedActions;

}
