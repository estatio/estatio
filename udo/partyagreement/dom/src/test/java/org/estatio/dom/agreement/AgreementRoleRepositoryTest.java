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

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction.FinderMethod;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import static org.assertj.core.api.Assertions.assertThat;

public class AgreementRoleRepositoryTest {

    public static class FindByAgreementAndPartyAndTypeAndContainsDate extends AgreementRoleRepositoryTest {

        private FinderInteraction finderInteraction;

        private Party party;

        private Agreement agreement;
        private AgreementRoleType type;
        private LocalDate date;

        private AgreementRoleRepository agreementRoleRepository;

        @Before
        public void setup() {

            party = new PartyForTesting();

            agreement = new AgreementForTesting();
            type = new AgreementRoleType();
            date = new LocalDate(2013, 4, 1);

            agreementRoleRepository = new AgreementRoleRepository() {

                @Override
                protected <T> T firstMatch(Query<T> query) {
                    finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                    return null;
                }

                @Override
                protected List<AgreementRole> allInstances() {
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

        @Test
        public void findByAgreementAndTypeAndContainsDate() {
            agreementRoleRepository.findByAgreementAndTypeAndContainsDate(agreement, type, date);
            // then
            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.FIRST_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(AgreementRole.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByAgreementAndTypeAndContainsDate");
            assertThat(finderInteraction.getArgumentsByParameterName().get("agreement")).isEqualTo(agreement);
            assertThat(finderInteraction.getArgumentsByParameterName().get("type")).isEqualTo(type);
            assertThat(finderInteraction.getArgumentsByParameterName().get("startDate")).isEqualTo(date);
            assertThat(finderInteraction.getArgumentsByParameterName().get("endDate")).isEqualTo(LocalDateInterval.endDateFromStartDate(date));
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(4);
        }

        @Test
        public void findByAgreementAndPartyAndTypeAndStartDate() {
            agreementRoleRepository.findByAgreementAndPartyAndTypeAndContainsDate(agreement, party, type, date);
            // then
            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.FIRST_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(AgreementRole.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByAgreementAndPartyAndTypeAndContainsDate");
            assertThat(finderInteraction.getArgumentsByParameterName().get("agreement")).isEqualTo(agreement);
            assertThat(finderInteraction.getArgumentsByParameterName().get("party")).isEqualTo(party);
            assertThat(finderInteraction.getArgumentsByParameterName().get("type")).isEqualTo(type);
            assertThat(finderInteraction.getArgumentsByParameterName().get("startDate")).isEqualTo(date);
            assertThat(finderInteraction.getArgumentsByParameterName().get("endDate")).isEqualTo(LocalDateInterval.endDateFromStartDate(date));
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(5);
        }

        @Test
        public void findByParty() {
            agreementRoleRepository.findByParty(party);
            // then
            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(AgreementRole.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByParty");
            assertThat(finderInteraction.getArgumentsByParameterName().get("party")).isEqualTo((Object) party);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }

        @Test
        public void findByPartyAndTypeAndContainsDate() {
            agreementRoleRepository.findByPartyAndTypeAndContainsDate(party, type, date);
            // then
            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(AgreementRole.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByPartyAndTypeAndContainsDate");
            assertThat(finderInteraction.getArgumentsByParameterName().get("party")).isEqualTo((Object) party);
            assertThat(finderInteraction.getArgumentsByParameterName().get("type")).isEqualTo((Object) type);
            assertThat(finderInteraction.getArgumentsByParameterName().get("startDate")).isEqualTo((Object) date);
            assertThat(finderInteraction.getArgumentsByParameterName().get("endDate")).isEqualTo((Object) LocalDateInterval.endDateFromStartDate(date));
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(4);
        }

    }

    public static class GetId {

        @Test
        public void getId() {
            assertThat(new AgreementRoleRepository().getId()).isEqualTo(AgreementRoleRepository.class.getName());
        }

    }

    public static class IconName {

        @Test
        public void iconName() {
            assertThat(new AgreementRoleRepository().iconName()).isEqualTo("AgreementRole");
        }

    }

}