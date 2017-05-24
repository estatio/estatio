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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.estatio.dom.party.role.PartyRoleType;
import org.estatio.dom.party.role.PartyRoleTypeData;
import org.estatio.dom.party.role.PartyRoleTypeRepository;
import org.estatio.integtests.EstatioIntegrationTest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import static org.assertj.core.api.Assertions.assertThat;

public class PartyRoleTypeRepostitory_IntegTest extends EstatioIntegrationTest {

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public static class FindOrCreate extends PartyRoleTypeRepostitory_IntegTest {

        @Test
        public void happy_case() {
            //When
            final PartyRoleTypeEnum testRole = PartyRoleTypeEnum.TEST_ROLE;
            final PartyRoleType partyRoleType = partyRoleTypeRepository.findOrCreate(testRole);
            partyRoleTypeRepository.findOrCreate(testRole);
            //Then
            assertThat(partyRoleType.getTitle()).isEqualTo(testRole.getTitle());
            assertThat(partyRoleTypeRepository.findByKey(testRole.getKey())).isNotNull();
        }
    }

    @AllArgsConstructor
    public enum PartyRoleTypeEnum implements PartyRoleTypeData {
        TEST_ROLE("Landlord");

        public PartyRoleType findUsing(PartyRoleTypeRepository repo) {
            return Util.findUsing(this, repo);
        }

        @Override
        public String getKey() {
            return this.name();
        }

        @Getter
        private String title;

    }

}