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
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
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

                    executionContext.executeChild(this, Person_enum.DylanOfficeAdministratorGb.toFixtureScript()); // gb mailroom
                    executionContext.executeChild(this, Person_enum.JonathanPropertyManagerGb.toFixtureScript());  // gb property mgr for OXF
                    executionContext.executeChild(this, Person_enum.FaithConwayGb.toFixtureScript());  // gb country administrator
                    executionContext.executeChild(this, Person_enum.OscarCountryDirectorGb.toFixtureScript());  // gb country director
                    executionContext.executeChild(this, Person_enum.EmmaTreasurerGb.toFixtureScript());   // gb treasurer
                    executionContext.executeChild(this, Person_enum.ThibaultOfficerAdministratorFr.toFixtureScript());  // fr mailroom
                    executionContext.executeChild(this, Person_enum.FifineLacroixFr.toFixtureScript());  // fr property mgr for VIV and MNS
                    executionContext.executeChild(this, Person_enum.OlivePropertyManagerFr.toFixtureScript());  // fr property mgr for MAC
                    executionContext.executeChild(this, Person_enum.RosaireEvrardFr.toFixtureScript());  // fr country administrator
                    executionContext.executeChild(this, Person_enum.GabrielHerveFr.toFixtureScript());  // fr country director
                    executionContext.executeChild(this, Person_enum.BrunoTreasurerFr.toFixtureScript()); // fr treasurer
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