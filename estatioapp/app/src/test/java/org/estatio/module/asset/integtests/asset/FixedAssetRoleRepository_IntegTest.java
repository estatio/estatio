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
package org.estatio.module.asset.integtests.asset;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleRepository;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.asset.integtests.AssetModuleIntegTestAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;

import static org.junit.Assert.assertNotNull;

public class FixedAssetRoleRepository_IntegTest extends AssetModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.builder());
            }
        });
    }

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    FixedAssetRoleRepository fixedAssetRoleRepository;

    public static class FindRole_AssetAndType extends FixedAssetRoleRepository_IntegTest {

        @Test
        public void withExistingPropertyAndRoleType() throws Exception {

            // given
            Property property = propertyRepository.findPropertyByReference("OXF");

            // when
            FixedAssetRole propertyActor = fixedAssetRoleRepository.findRole(property, FixedAssetRoleTypeEnum.PROPERTY_OWNER);

            // then
            assertNotNull(propertyActor);
        }
    }

    public static class FindRole_AssetAndPartyAndType extends FixedAssetRoleRepository_IntegTest {

        @Test
        public void withExistingPropertyPartyAndRoleType() throws Exception {

            // given
            Party party = OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry);
            Property property = propertyRepository.findPropertyByReference("OXF");
            // TODO: get right dates (although the date params are not actually
            // used in the query..)
            LocalDate startDate = new LocalDate();
            LocalDate endDate = new LocalDate();

            // when
            FixedAssetRole propertyActor = fixedAssetRoleRepository.findRole(property, party, FixedAssetRoleTypeEnum.PROPERTY_OWNER, startDate, endDate);

            // then
            Assert.assertNotNull(propertyActor);
        }

    }

}
