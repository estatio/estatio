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
package org.estatio.integtests.assets;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.FixedAssetRole;
import org.estatio.dom.asset.FixedAssetRoleType;
import org.estatio.dom.asset.FixedAssetRoles;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset._PropertyForOxfGb;
import org.estatio.fixture.party.OrganisationForHelloWorldNl;
import org.estatio.integtests.EstatioIntegrationTest;

public class FixedAssetRolesTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new _PropertyForOxfGb());
            }
        });
    }

    @Inject
    Properties properties;

    @Inject
    Parties parties;

    @Inject
    FixedAssetRoles fixedAssetRoles;

    public static class FindRole_AssetAndType extends FixedAssetRolesTest {

        @Test
        public void withExistingPropertyAndRoleType() throws Exception {

            // given
            Property property = properties.findPropertyByReference("OXF");

            // when
            FixedAssetRole propertyActor = fixedAssetRoles.findRole(property, FixedAssetRoleType.PROPERTY_OWNER);

            // then
            assertNotNull(propertyActor);
        }
    }

    public static class FindRole_AssetAndPartyAndType extends FixedAssetRolesTest {

        @Test
        public void withExistingPropertyPartyAndRoleType() throws Exception {

            // given
            Party party = parties.findPartyByReference(OrganisationForHelloWorldNl.REF);
            Property property = properties.findPropertyByReference("OXF");
            // TODO: get right dates (although the date params are not actually
            // used in the query..)
            LocalDate startDate = new LocalDate();
            LocalDate endDate = new LocalDate();

            // when
            FixedAssetRole propertyActor = fixedAssetRoles.findRole(property, party, FixedAssetRoleType.PROPERTY_OWNER, startDate, endDate);

            // then
            Assert.assertNotNull(propertyActor);
        }

    }

}
