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
package org.estatio.dom.party;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction.FinderMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class PartyRepositoryTest {

    FinderInteraction finderInteraction;

    PartyRepository partyRepository;

    @Before
    public void setup() {

        partyRepository = new PartyRepository() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<Party> allInstances() {
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

    public static class MatchPartyByReferenceOrName extends PartyRepositoryTest {
        @Test
        public void byReferenceWildcard() {

            partyRepository.matchPartyByReferenceOrName("*REF?1*");

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.FIRST_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(Party.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("matchByReferenceOrName");
            assertThat(finderInteraction.getArgumentsByParameterName().get("referenceOrName")).isEqualTo((Object) "(?i).*REF.1.*");

            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }
    }

    public static class FindParties extends PartyRepositoryTest {

        @Test
        public void findParties() {

            partyRepository.findParties("*REF?1*");

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(Party.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("matchByReferenceOrName");
            assertThat(finderInteraction.getArgumentsByParameterName().get("referenceOrName")).isEqualTo((Object) "(?i).*REF.1.*");

            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);
        }
    }

    public static class AllParties extends PartyRepositoryTest {

        @Test
        public void allParties() {

            partyRepository.allParties();

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_INSTANCES);
        }
    }

    public static class ValidateNewParty extends OrganisationRepositoryTest {

        PartyRepository partyRepository;

        @Before
        public void setup() {
            partyRepository = new PartyRepository() {
                @Override
                public Party findPartyByReference(final String reference) {
                    Party result = new Party() {
                        @Override public ApplicationTenancy getApplicationTenancy() {
                            return null;
                        }
                    };
                    return result;
                }
            };
        }

        @Test
        public void testValidateNewParty() {

            // given
            String reference = "some reference";
            // when findPartyByReference( .. ) returns a result
            assertNotNull(partyRepository.findPartyByReference(reference));
            // then
            assertThat(partyRepository.validateNewParty(reference)).isEqualTo("Reference should be unique; does similar party already exist?");

        }

    }

}
