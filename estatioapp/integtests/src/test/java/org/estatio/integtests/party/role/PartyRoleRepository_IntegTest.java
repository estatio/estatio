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
package org.estatio.integtests.party.role;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.role.FixedAssetRoleRepository;
import org.estatio.dom.asset.role.FixedAssetRoleTypeEnum;
import org.estatio.dom.party.PartyRoleTypeEnum;
import org.estatio.dom.party.role.IPartyRoleType;
import org.estatio.dom.party.role.PartyRoleRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.PersonForBrunoJeremieFr;
import org.estatio.fixture.party.PersonForDylanClaytonGb;
import org.estatio.fixture.party.PersonForEmmaFarmerGb;
import org.estatio.fixture.party.PersonForFaithConwayGb;
import org.estatio.fixture.party.PersonForFifineLacroixFr;
import org.estatio.fixture.party.PersonForGabrielHerveFr;
import org.estatio.fixture.party.PersonForJonathanRiceGb;
import org.estatio.fixture.party.PersonForOliveBeausoleilFr;
import org.estatio.fixture.party.PersonForOscarPritchardGb;
import org.estatio.fixture.party.PersonForRosaireEvrardFr;
import org.estatio.fixture.party.PersonForThibaultJosueFr;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class PartyRoleRepository_IntegTest extends EstatioIntegrationTest {

    @Inject
    PartyRoleRepository partyRoleRepository;
    @Inject
    FixedAssetRoleRepository fixedAssetRoleRepository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public static class FindOrCreate extends PartyRoleRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(FixtureScript.ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new PersonForDylanClaytonGb()); // gb mailroom
                    executionContext.executeChild(this, new PersonForJonathanRiceGb());  // gb property mgr for OXF
                    executionContext.executeChild(this, new PersonForFaithConwayGb());  // gb country administrator
                    executionContext.executeChild(this, new PersonForOscarPritchardGb());  // gb country director
                    executionContext.executeChild(this, new PersonForEmmaFarmerGb());   // gb treasurer

                    executionContext.executeChild(this, new PersonForThibaultJosueFr());  // fr mailroom
                    executionContext.executeChild(this, new PersonForFifineLacroixFr());  // fr property mgr for VIV and MNS
                    executionContext.executeChild(this, new PersonForOliveBeausoleilFr());  // fr property mgr for MAC
                    executionContext.executeChild(this, new PersonForRosaireEvrardFr());  // fr country administrator
                    executionContext.executeChild(this, new PersonForGabrielHerveFr());  // fr country director
                    executionContext.executeChild(this, new PersonForBrunoJeremieFr()); // fr treasurer
                }
            });
        }

        @Test
        public void happy_case() {
            assertPartyRoleSize(PartyRoleTypeEnum.MAIL_ROOM, 2);
            assertPartyRoleSize(FixedAssetRoleTypeEnum.PROPERTY_MANAGER, 3);
            assertPartyRoleSize(PartyRoleTypeEnum.COUNTRY_ADMINISTRATOR, 2);
            assertPartyRoleSize(PartyRoleTypeEnum.COUNTRY_DIRECTOR, 2);
        }

        private void assertPartyRoleSize(final IPartyRoleType type, final int expected) {
            assertThat(partyRoleRepository.findByRoleType(type)).hasSize(expected);
        }
    }


}