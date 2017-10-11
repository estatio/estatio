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
package org.estatio.integtests.party;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.role.FixedAssetRoleTypeEnum;
import org.estatio.dom.party.PartyRoleTypeEnum;
import org.estatio.dom.party.PersonRepository;
import org.estatio.dom.party.role.IPartyRoleType;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.PersonForBrunoTreasurerFr;
import org.estatio.fixture.party.PersonForDylanOfficeAdministratorGb;
import org.estatio.fixture.party.PersonForEmmaTreasurerGb;
import org.estatio.fixture.party.PersonForFaithConwayGb;
import org.estatio.fixture.party.PersonForFifineLacroixFr;
import org.estatio.fixture.party.PersonForGabrielHerveFr;
import org.estatio.fixture.party.PersonForJonathanPropertyManagerGb;
import org.estatio.fixture.party.PersonForOlivePropertyManagerFr;
import org.estatio.fixture.party.PersonForOscarCountryDirectorGb;
import org.estatio.fixture.party.PersonForRosaireEvrardFr;
import org.estatio.fixture.party.PersonForThibaultOfficerAdministratorFr;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonRepository_IntegTest extends EstatioIntegrationTest {

    @Inject
    PersonRepository personRepository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public static class FindOrCreate extends PersonRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new PersonForDylanOfficeAdministratorGb()); // gb mailroom
                    executionContext.executeChild(this, new PersonForJonathanPropertyManagerGb());  // gb property mgr for OXF
                    executionContext.executeChild(this, new PersonForFaithConwayGb());  // gb country administrator
                    executionContext.executeChild(this, new PersonForOscarCountryDirectorGb());  // gb country director
                    executionContext.executeChild(this, new PersonForEmmaTreasurerGb());   // gb treasurer

                    executionContext.executeChild(this, new PersonForThibaultOfficerAdministratorFr());  // fr mailroom
                    executionContext.executeChild(this, new PersonForFifineLacroixFr());  // fr property mgr for VIV and MNS
                    executionContext.executeChild(this, new PersonForOlivePropertyManagerFr());  // fr property mgr for MAC
                    executionContext.executeChild(this, new PersonForRosaireEvrardFr());  // fr country administrator
                    executionContext.executeChild(this, new PersonForGabrielHerveFr());  // fr country director
                    executionContext.executeChild(this, new PersonForBrunoTreasurerFr()); // fr treasurer
                }
            });
        }

        @Test
        public void happy_case() {
            assertSize(PartyRoleTypeEnum.OFFICE_ADMINISTRATOR, "/FRA", 1);
            assertSize(PartyRoleTypeEnum.OFFICE_ADMINISTRATOR, "/GBR", 1);
            assertSize(FixedAssetRoleTypeEnum.PROPERTY_MANAGER, "/GBR", 1);
            assertSize(PartyRoleTypeEnum.OFFICE_ADMINISTRATOR, "/FRA", 1);
        }

        private void assertSize(final IPartyRoleType type, final String atPath, final int expected) {
            assertThat(personRepository.findByRoleTypeAndAtPath(type, atPath)).hasSize(expected);
        }
    }


}