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
package org.estatio.dom.agreement;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction.FinderMethod;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

import static org.assertj.core.api.Assertions.assertThat;

public class AgreementRepositoryTest {

    FinderInteraction finderInteraction;

    AgreementRepository agreementRepository;

    AgreementType agreementType;
    AgreementRoleType agreementRoleType;
    Party party;

    @Before
    public void setup() {

        agreementType = new AgreementType();
        agreementRoleType = new AgreementRoleType();
        party = new PartyForTesting();

        agreementRepository = new AgreementRepository() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<Agreement> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };
    }

    public static class FindByAgreementTypeAndRoleTypeAndParty extends AgreementRepositoryTest {

        @Test
        public void happyCase() {

            agreementRepository.findByAgreementTypeAndRoleTypeAndParty(agreementType, agreementRoleType, party);

            // then
            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(Agreement.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByAgreementTypeAndRoleTypeAndParty");
            assertThat(finderInteraction.getArgumentsByParameterName().get("agreementType")).isEqualTo((Object) agreementType);
            assertThat(finderInteraction.getArgumentsByParameterName().get("roleType")).isEqualTo((Object) agreementRoleType);
            assertThat(finderInteraction.getArgumentsByParameterName().get("party")).isEqualTo((Object) party);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(3);
        }

    }

    public static class FindByTypeAndReferenceOrName extends AgreementRepositoryTest {

        @Test
        public void happyCase() {
            agreementRepository.findByTypeAndReferenceOrName(agreementType, "");

            // then
            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(Agreement.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByTypeAndReferenceOrName");
            assertThat(finderInteraction.getArgumentsByParameterName().get("agreementType")).isEqualTo((Object) agreementType);
            assertThat(finderInteraction.getArgumentsByParameterName().get("regex")).isEqualTo((Object) "");
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(2);
        }

    }

}