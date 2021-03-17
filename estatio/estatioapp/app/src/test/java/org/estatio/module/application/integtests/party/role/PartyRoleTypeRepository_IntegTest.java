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
package org.estatio.module.application.integtests.party.role;

import java.util.List;

import javax.inject.Inject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.estatio.module.application.integtests.ApplicationModuleIntegTestAbstract;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.capex.dom.project.ProjectRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseRoleTypeEnum;
import org.estatio.module.party.dom.relationship.PartyRelationshipTypeEnum;
import org.estatio.module.party.dom.role.PartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class PartyRoleTypeRepository_IntegTest extends ApplicationModuleIntegTestAbstract {

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public static class Seeded extends PartyRoleTypeRepository_IntegTest {

        @Test
        public void happy_case() {

            // given that data has been seeded,
            // and this includes the party role types across all implementations of IPartyRoleType...

            // when
            final List<PartyRoleType> partyRoleType = partyRoleTypeRepository.listAll();

            // then
            assertThat(partyRoleType.size()).isEqualTo(
                            FixedAssetRoleTypeEnum.values().length +
                            ProjectRoleTypeEnum.values().length +
                            PartyRoleTypeEnum.values().length +
                            PartyRelationshipTypeEnum.values().length +
                            LeaseRoleTypeEnum.values().length +
                            IncomingInvoiceRoleTypeEnum.values().length
            );
        }
    }


}