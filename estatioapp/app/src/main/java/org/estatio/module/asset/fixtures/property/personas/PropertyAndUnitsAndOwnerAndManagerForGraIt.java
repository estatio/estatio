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
package org.estatio.module.asset.fixtures.property.personas;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.PropertyType;
import org.estatio.module.asset.fixtures.PropertyAndUnitsAndOwnerAndManagerAbstract;
import org.estatio.module.asset.fixtures.property.builders.PropertyAndUnitsAndOwnerAndManagerBuilder;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndOwnerAndManager_enum;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

import lombok.Getter;

public class PropertyAndUnitsAndOwnerAndManagerForGraIt extends PropertyAndUnitsAndOwnerAndManagerAbstract {

    public static final PropertyAndOwnerAndManager_enum data = PropertyAndOwnerAndManager_enum.GraIt;

    public static final String REF = data.getRef();
    public static final String PARTY_REF_OWNER = data.getOwner().getRef();
    public static final String PARTY_REF_MANAGER = data.getManager().getRef();
    public static final String AT_PATH_COUNTRY = data.getApplicationTenancy().getPath();
    @Getter
    public Property property;
    @Inject
    protected CountryRepository countryRepository;
    @Inject
    protected PropertyRepository propertyRepository;
    @Inject
    protected PartyRepository partyRepository;
    @Inject
    protected ApplicationTenancyRepository applicationTenancyRepository;

    public static String unitReference(final String suffix) {
        return REF + "-" + suffix;
    }

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, data.getOwner().toFixtureScript());
        executionContext.executeChild(this, data.getManager().toFixtureScript());

        // exec
        final Party owner = partyRepository.findPartyByReference(data.getOwner().getRef());
        final Party manager = partyRepository.findPartyByReference(data.getManager().getRef());
        final Country country = data.getCountry().upsertUsing(serviceRegistry);

        createPropertyAndUnits(
                data.getApplicationTenancy().getPath(),
                data.getRef(), data.getName(), data.getCity(), country, data.getShoppingCenter(),
                data.getNumberOfUnits(), data.getOpeningDate(), data.getAcquireDate(), owner, manager,
                data.getLocationStr(),
                executionContext);
    }

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

        return property;
    }

    public Property getProperty() {
        return this.property;
    }
}
