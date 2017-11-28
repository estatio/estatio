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

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.PropertyAndUnitsAndOwnerAndManagerAbstract;
import org.estatio.module.asset.fixtures.property.builders.PropertyAndUnitsAndOwnerAndManagerBuilder;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndOwnerAndManager_enum;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

import lombok.Getter;

public class PropertyAndUnitsAndOwnerAndManagerForBudNl extends PropertyAndUnitsAndOwnerAndManagerAbstract {

    public static final PropertyAndOwnerAndManager_enum data = PropertyAndOwnerAndManager_enum.BudNl;

    public static final String REF = data.getRef();
    public static final String PARTY_REF_MANAGER = data.getManager().getRef();

    @Getter
    public Property property;

    public static String unitReference(String suffix) {
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

        PropertyAndUnitsAndOwnerAndManagerBuilder propertyAndUnitsAndOwnerAndManagerBuilder = new PropertyAndUnitsAndOwnerAndManagerBuilder();

        property = propertyAndUnitsAndOwnerAndManagerBuilder
                .setReference(data.getRef())
                .setName(data.getName())
                .setCity(data.getCity())
                .setCountry(country)
                .setPropertyType(data.getShoppingCenter())
                .setNumberOfUnits(data.getNumberOfUnits())
                .setOpeningDate(data.getOpeningDate())
                .setAcquireDate(data.getAcquireDate())
                .setOwner(owner)
                .setOwnerStartDate(data.getOwnerStartDate())
                .setOwnerEndDate(data.getOwnerEndDate())
                .setManager(manager)
                .setManagerStartDate(data.getManagerStartDate())
                .setManagerEndDate(data.getManagerEndDate())
                .setLocationStr(data.getLocationStr())
                .build(this, executionContext)
                .getProperty();
    }

    @Inject
    protected PartyRepository partyRepository;

}
