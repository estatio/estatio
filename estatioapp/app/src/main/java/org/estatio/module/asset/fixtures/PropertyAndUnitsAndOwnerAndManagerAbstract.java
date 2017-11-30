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

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.fixtures.property.builders.PropertyAndUnitsAndOwnerAndManagerBuilder;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.country.fixtures.enums.Country_enum;
import org.estatio.module.party.dom.Party;

import lombok.Getter;

/**
 * Sets up the {@link Property} and also a number of
 * {@link Unit}s.
 */
public abstract class PropertyAndUnitsAndOwnerAndManagerAbstract extends FixtureScript {

    private final PropertyAndUnitsAndOwnerAndManager_enum data;

    @Getter
    public Property property;

    protected PropertyAndUnitsAndOwnerAndManagerAbstract(final PropertyAndUnitsAndOwnerAndManager_enum data) {
        this.data = data;
    }

    @Override
    protected void execute(final ExecutionContext ec) {

        final Party owner = ec.executeChildT(this, data.getOwner_d().toFixtureScript()).getObject();
        final Party manager = ec.executeChildT(this, data.getManager_d().toFixtureScript()).getObject();

        final Property_enum property_d = data.getProperty_d();
        final Country_enum country_d = property_d.getCountry_d();
        final Country country = ec.executeChildT(this, country_d.toFixtureScript()).getObject();

        property = new PropertyAndUnitsAndOwnerAndManagerBuilder()
                .setReference(data.getRef())

                .setName(property_d.getName())
                .setCity(property_d.getCity())
                .setCountry(country)
                .setPropertyType(property_d.getPropertyType())
                .setOpeningDate(property_d.getOpeningDate())
                .setAcquireDate(property_d.getAcquireDate())
                .setLocationStr(property_d.getLocationStr())

                .setNumberOfUnits(data.getNumberOfUnits())

                .setOwner(owner)
                .setOwnerStartDate(data.getOwnerStartDate())
                .setOwnerEndDate(data.getOwnerEndDate())

                .setManager(manager)
                .setManagerStartDate(data.getManagerStartDate())
                .setManagerEndDate(data.getManagerEndDate())

                .build(this, ec)
                .getObject();
    }

}