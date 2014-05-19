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

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyType;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;
import org.estatio.dom.party.Party;

import static org.estatio.integtests.VT.ld;

public class PropertiesAndUnitsForOxf extends PropertiesAndUnitsAbstract {

    @Override
    protected void execute(ExecutionContext executionContext) {
        Party owner = parties.findPartyByReference("HELLOWORLD");
        Party manager = parties.findPartyByReference("JDOE");

        Country country = countries.findCountry("GBR");
        Property prop1 = createPropertyAndUnits(
                "OXF", "Oxford Super Mall", "Oxford", country, PropertyType.SHOPPING_CENTER, 25,
                ld(1999, 1, 1), ld(2008, 6, 1), owner, manager, 51.74579, -1.24334,
                executionContext);

        State state = states.findState("GB-OXF");
        communicationChannelContributedActions.newPostal(prop1, CommunicationChannelType.POSTAL_ADDRESS, prop1.getCountry(), state, "1 Market Street", null, null, "OX1 3HL", "Oxford");
        communicationChannelContributedActions.newPhoneOrFax(prop1, CommunicationChannelType.PHONE_NUMBER, "+44 123 456789");
        communicationChannelContributedActions.newPhoneOrFax(prop1, CommunicationChannelType.FAX_NUMBER, "+44 987 654321");
        communicationChannelContributedActions.newEmail(prop1, CommunicationChannelType.EMAIL_ADDRESS, "info@oxford.example.com");
    }

}
