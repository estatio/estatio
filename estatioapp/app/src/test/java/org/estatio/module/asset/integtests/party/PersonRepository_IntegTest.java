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
package org.estatio.module.asset.integtests.party;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForBrunoTreasurerFr;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForDylanOfficeAdministratorGb;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForEmmaTreasurerGb;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForFaithConwayGb;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForFifineLacroixFr;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForGabrielHerveFr;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForJonathanPropertyManagerGb;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForOlivePropertyManagerFr;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForOscarCountryDirectorGb;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForRosaireEvrardFr;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForThibaultOfficerAdministratorFr;
import org.estatio.module.asset.integtests.AssetModuleIntegTestAbstract;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonRepository_IntegTest extends AssetModuleIntegTestAbstract {

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

                    executionContext.executeChild(this, new PersonAndRolesForDylanOfficeAdministratorGb()); // gb mailroom
                    executionContext.executeChild(this, new PersonAndRolesForJonathanPropertyManagerGb());  // gb property mgr for OXF
                    executionContext.executeChild(this, new PersonAndRolesForFaithConwayGb());  // gb country administrator
                    executionContext.executeChild(this, new PersonAndRolesForOscarCountryDirectorGb());  // gb country director
                    executionContext.executeChild(this, new PersonAndRolesForEmmaTreasurerGb());   // gb treasurer

                    executionContext.executeChild(this, new PersonAndRolesForThibaultOfficerAdministratorFr());  // fr mailroom
                    executionContext.executeChild(this, new PersonAndRolesForFifineLacroixFr());  // fr property mgr for VIV and MNS
                    executionContext.executeChild(this, new PersonAndRolesForOlivePropertyManagerFr());  // fr property mgr for MAC
                    executionContext.executeChild(this, new PersonAndRolesForRosaireEvrardFr());  // fr country administrator
                    executionContext.executeChild(this, new PersonAndRolesForGabrielHerveFr());  // fr country director
                    executionContext.executeChild(this, new PersonAndRolesForBrunoTreasurerFr()); // fr treasurer
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