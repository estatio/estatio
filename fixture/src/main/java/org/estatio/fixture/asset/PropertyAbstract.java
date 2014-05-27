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
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.States;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import static org.estatio.integtests.VT.ld;

/**
 * Sets up the {@link org.estatio.dom.asset.Property} and also a number of
 * {@link org.estatio.dom.asset.Unit}s.
 */
public abstract class PropertyAbstract extends FixtureScript {

    protected Property createPropertyAndUnits(final String reference, String name, String city, Country country, PropertyType type, int numberOfUnits, LocalDate openingDate, LocalDate acquireDate, Party owner, Party manager, double lat, double lng, ExecutionContext fixtureResults) {
        Property property = properties.newProperty(reference, name, type, city, country, acquireDate);
        property.setOpeningDate(openingDate);
        property.addRoleIfDoesNotExist(owner, FixedAssetRoleType.PROPERTY_OWNER, ld(1999, 1, 1), ld(2000, 1, 1));
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
    protected States states;

    @Inject
    protected Countries countries;

    @Inject
    protected Properties properties;

    @Inject
    protected Parties parties;

    @Inject
    protected CommunicationChannelContributions communicationChannelContributedActions;

}
