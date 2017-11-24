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
package org.estatio.module.asset.fixtures;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.dom.impl.StateRepository;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.PropertyType;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

import lombok.Getter;

/**
 * Sets up the {@link Property} and also a number of
 * {@link Unit}s.
 */
public abstract class PropertyAndOwnerAndManagerAbstract extends FixtureScript {

    @Getter
    public Property property;

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
            final ExecutionContext executionContext) {

        PropertyAndUnitsAndOwnerAndManagerBuilder propertyAndUnitsAndOwnerAndManagerBuilder = new PropertyAndUnitsAndOwnerAndManagerBuilder();

        property = propertyAndUnitsAndOwnerAndManagerBuilder
                .setReference(reference)
                .setName(name)
                .setCity(city)
                .setCountry(country)
                .setPropertyType(type)
                .setNumberOfUnits(numberOfUnits)
                .setOpeningDate(openingDate)
                .setAcquireDate(acquireDate)
                .setOwner(owner)
                .setManager(manager)
                .setLocationStr(locationStr)
                .build(this, executionContext)
                .getProperty();

        /*
        Property property = propertyRepository.newProperty(reference, name, type, city, country, acquireDate);
        property.setOpeningDate(openingDate);
        property.setLocation(Location.fromString(locationStr));
        property.addRoleIfDoesNotExist(owner, FixedAssetRoleTypeEnum.PROPERTY_OWNER, ld(1999, 1, 1), ld(2000, 1, 1));
        property.addRoleIfDoesNotExist(manager, FixedAssetRoleTypeEnum.ASSET_MANAGER, null, null);
        for (int i = 0; i < numberOfUnits; i++) {
            int unitNumber = i + 1;
            property.newUnit(String.format("%s-%03d", reference, unitNumber), "Unit " + unitNumber, unitType(i)).setArea(new BigDecimal((i + 1) * 100));
        }

        this.property = property;

        return executionContext.addResult(this, property.getReference(), property);
        */

        return property;
    }

//    private UnitType unitType(int n) {
//        final UnitType[] unitTypes = UnitType.values();
//        return unitTypes[n % unitTypes.length];
//    }
//
    // //////////////////////////////////////

    @Inject
    protected StateRepository stateRepository;

    @Inject
    protected CountryRepository countryRepository;

    @Inject
    protected PropertyRepository propertyRepository;

    @Inject
    protected PartyRepository partyRepository;

    @Inject
    protected ApplicationTenancyRepository applicationTenancyRepository;

}
