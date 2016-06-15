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

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;
import org.isisaddons.wicket.gmap3.cpt.applib.Location;
import org.joda.time.LocalDate;
import org.estatio.dom.asset.FixedAssetRoleType;
import org.estatio.dom.asset.PropertyMenu;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyType;
import org.estatio.dom.asset.UnitType;
import org.estatio.dom.geography.CountryRepository;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.StateRepository;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioFixtureScript;

import static org.estatio.integtests.VT.ld;

/**
 * Sets up the {@link org.estatio.dom.asset.Property} and also a number of
 * {@link org.estatio.dom.asset.Unit}s.
 */
public abstract class PropertyAbstract extends EstatioFixtureScript {

    protected Property createPropertyAndUnits(
            final String atPath,
            final String reference,
            final String name,
            final String city,
            final Country country,
            final PropertyType type,
            final int numberOfUnits,
            final LocalDate openingDate,
            final LocalDate acquireDate,
            final Party owner,
            final Party manager,
            final String locationStr,
            final ExecutionContext fixtureResults) {

        final ApplicationTenancy applicationTenancy = applicationTenancyRepository.findByPath(atPath);
        Property property = propertyMenu.newProperty(reference, name, type, city, country, acquireDate);
        property.setOpeningDate(openingDate);
        property.setLocation(Location.fromString(locationStr));
        property.addRoleIfDoesNotExist(owner, FixedAssetRoleType.PROPERTY_OWNER, ld(1999, 1, 1), ld(2000, 1, 1));
        property.addRoleIfDoesNotExist(manager, FixedAssetRoleType.ASSET_MANAGER, null, null);
        for (int i = 0; i < numberOfUnits; i++) {
            int unitNumber = i + 1;
            property.newUnit(String.format("%s-%03d", reference, unitNumber), "Unit " + unitNumber, unitType(i)).setArea(new BigDecimal((i + 1) * 100));
        }
        return fixtureResults.addResult(this, property.getReference(), property);
    }

    private UnitType unitType(int n) {
        final UnitType[] unitTypes = UnitType.values();
        return unitTypes[n % unitTypes.length];
    }

    // //////////////////////////////////////

    @Inject
    protected StateRepository stateRepository;

    @Inject
    protected CountryRepository countryRepository;

    @Inject
    protected PropertyMenu propertyMenu;

    @Inject
    protected Parties parties;

    @Inject
    protected ApplicationTenancyRepository applicationTenancyRepository;

}
