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
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AgreementsTest {

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


    public static class FindAgreementByReference extends AgreementsTest {

        @Test
        public void happyCase() {

            agreementRepository.findAgreementByReference("*some?Reference*");

            // then
            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Agreement.class));
            assertThat(finderInteraction.getQueryName(), is("findByReference"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("reference"), is((Object) "*some?Reference*"));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }
    }

    public static class FindByAgreementTypeAndRoleTypeAndParty extends AgreementsTest {

        @Test
        public void happyCase() {

            agreementRepository.findByAgreementTypeAndRoleTypeAndParty(agreementType, agreementRoleType, party);

            // then
            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Agreement.class));
            assertThat(finderInteraction.getQueryName(), is("findByAgreementTypeAndRoleTypeAndParty"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("agreementType"), is((Object) agreementType));
            assertThat(finderInteraction.getArgumentsByParameterName().get("roleType"), is((Object) agreementRoleType));
            assertThat(finderInteraction.getArgumentsByParameterName().get("party"), is((Object) party));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(3));
        }

    }

    public static class FindByTypeAndReferenceOrName extends AgreementsTest {

        @Test
        public void happyCase() {
            agreementRepository.findByTypeAndReferenceOrName(agreementType, "");

            // then
            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Agreement.class));
            assertThat(finderInteraction.getQueryName(), is("findByTypeAndReferenceOrName"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("agreementType"), is((Object) agreementType));
            assertThat(finderInteraction.getArgumentsByParameterName().get("regex"), is((Object) ""));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
        }

    }
}