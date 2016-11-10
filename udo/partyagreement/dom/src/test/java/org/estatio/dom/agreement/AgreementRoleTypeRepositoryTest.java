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

import org.apache.isis.applib.events.system.FixturesInstallingEvent;
import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.services.fixturespec.FixtureScriptsDefault;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction.FinderMethod;

import static org.assertj.core.api.Assertions.assertThat;

public class AgreementRoleTypeRepositoryTest {

    FinderInteraction finderInteraction;

    AgreementRoleTypeRepository agreementRoleTypeRepository;

    AgreementType agreementType;

    @Before
    public void setup() {
        agreementType = new AgreementType();

        agreementRoleTypeRepository = new AgreementRoleTypeRepository() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<AgreementRoleType> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };

        QueryResultsCache.Control control = new QueryResultsCache.Control();
        control.on(new FixturesInstallingEvent(new FixtureScriptsDefault()));

        final QueryResultsCache queryResultsCache = new QueryResultsCache() {
            {
                control = new Control();
            }
        };
        agreementRoleTypeRepository.queryResultsCache = queryResultsCache;
    }

    public static class FindApplicableTo extends AgreementRoleTypeRepositoryTest {

        @Test
        public void happyCase() {

            agreementRoleTypeRepository.findApplicableTo(agreementType);

            // then
            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(AgreementRoleType.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByAgreementType");
            assertThat(finderInteraction.getArgumentsByParameterName().get("agreementType")).isEqualTo((Object) agreementType);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }
    }

    public static class FindByTitle extends AgreementRoleTypeRepositoryTest {

        @Test
        public void happyCase() {

            agreementRoleTypeRepository.findByTitle("someTitle");

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.FIRST_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(AgreementRoleType.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByTitle");
            assertThat(finderInteraction.getArgumentsByParameterName().get("title")).isEqualTo((Object) "someTitle");
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }

    }
}