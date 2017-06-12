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

import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.party.role.IPartyRoleType;
import org.estatio.dom.party.role.PartyRoleRepository;
import org.estatio.dom.party.role.PartyRoleType;
import org.estatio.dom.party.role.PartyRoleTypeRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.OrganisationForAcmeNl;
import org.estatio.integtests.EstatioIntegrationTest;

import lombok.AllArgsConstructor;
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
            PartyRoleType partyRoleType = partyRoleTypeRepository.findOrCreate(PartyRoleTypeEnum.TEST_ROLE);
            Party party = partyRepository.findPartyByReference(OrganisationForAcmeNl.REF);
            int roleChoices = party.choices0AddRole().size();
            // When
            party.addRole(PartyRoleTypeEnum.TEST_ROLE);
            party.addRole(PartyRoleTypeEnum.TEST_ROLE); // Yes, it's idempotent
            // Then
            assertThat(partyRoleRepository.findbyParty(party).size()).isEqualTo(1);
            assertThat(partyRoleRepository.findbyRoleType(partyRoleType).size()).isEqualTo(1);
            assertThat(party.choices0AddRole().size()).isEqualTo(roleChoices-1); // We have one less choice
        }

    }

    @AllArgsConstructor
    public enum PartyRoleTypeEnum implements IPartyRoleType {
        TEST_ROLE("Landlord");

        @Override
        public String getKey() {
            return this.name();
        }

        private String title;

    }

}