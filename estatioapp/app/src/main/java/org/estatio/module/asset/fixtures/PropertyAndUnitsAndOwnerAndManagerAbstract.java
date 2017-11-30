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

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;

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

        data.toFixtureScript().build(this, ec);

//        property = new PropertyAndUnitsAndOwnerAndManagerBuilder()
//                .setReference(data.getRef())
//
//                .setName(data.getProperty_d().getName())
//                .setCity(data.getProperty_d().getCity())
//                .setPrereq((f,ex) -> f.setCountry(f.objectFor(data.getProperty_d().getCountry_d(), ex)))
//                .setPropertyType(data.getProperty_d().getPropertyType())
//                .setOpeningDate(data.getProperty_d().getOpeningDate())
//                .setAcquireDate(data.getProperty_d().getAcquireDate())
//                .setLocationStr(data.getProperty_d().getLocationStr())
//
//                .setNumberOfUnits(data.getNumberOfUnits())
//
//                .setPrereq((f,ex) -> f.setOwner(f.objectFor(data.getOwner_d(), ex)))
//                .setOwnerStartDate(data.getOwnerStartDate())
//                .setOwnerEndDate(data.getOwnerEndDate())
//
//                .setPrereq((f,ex) -> f.setManager(f.objectFor(data.getManager_d(), ex)))
//                .setManagerStartDate(data.getManagerStartDate())
//                .setManagerEndDate(data.getManagerEndDate())
//
//                .build(this, ec)
//                .getObject();
    }

}