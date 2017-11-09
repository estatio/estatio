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

import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.party.dom.role.PartyRoleRepository;
import org.estatio.module.party.dom.role.PartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForAcmeNl;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class PartyRole_IntegTest extends EstatioIntegrationTest {

    @Inject
    PartyRepository partyRepository;

    @Inject
    PartyRoleRepository partyRoleRepository;

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public static class Remove extends PartyRole_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new OrganisationForAcmeNl());
                }
            });
        }

        @Test
        public void add_role() {
            // Given
            PartyRoleType partyRoleType = partyRoleTypeRepository.findOrCreate(PartyRoleTypeEnum.TREASURER);
            Party party = partyRepository.findPartyByReference(OrganisationForAcmeNl.REF);
            int roleChoices = party.choices0AddRole().size();

            // When
            party.addRole(PartyRoleTypeEnum.TREASURER);

            // Then
            assertThat(partyRoleRepository.findByParty(party).size()).isEqualTo(1);
            assertThat(partyRoleRepository.findByRoleType(partyRoleType).size()).isEqualTo(1);
            assertThat(party.choices0AddRole().size()).isEqualTo(roleChoices-1); // We have one less choice

            // When
            party.addRole(PartyRoleTypeEnum.TREASURER); // Yes, it's idempotent

            // Then
            assertThat(partyRoleRepository.findByParty(party).size()).isEqualTo(1);
            assertThat(partyRoleRepository.findByRoleType(partyRoleType).size()).isEqualTo(1);
            assertThat(party.choices0AddRole().size()).isEqualTo(roleChoices-1); // We have one less choice
        }

    }


}