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
package org.estatio.dom.financial;

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

public class FinancialAccountsTest {

    FinderInteraction finderInteraction;

    FinancialAccounts financialAccounts;

    Party party;

    @Before
    public void setup() {

        party = new PartyForTesting();

        financialAccounts = new FinancialAccounts() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }
            @Override
            protected List<FinancialAccount> allInstances() {
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

    public static class FindAccountByReference extends FinancialAccountsTest {

        @Test
        public void findAccountByReference() {

            financialAccounts.findAccountByReference("*REF?1*");

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(FinancialAccount.class));
            assertThat(finderInteraction.getQueryName(), is("findByReference"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("reference"), is((Object)"*REF?1*"));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class FindAccountsByTypeAndOwner extends FinancialAccountsTest {

        @Test
        public void findAccountsByTypeAndOwner() {

            financialAccounts.findAccountsByOwner(party);

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(FinancialAccount.class));
            assertThat(finderInteraction.getQueryName(), is("findByOwner"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("owner"), is((Object)party));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class FindAccountsByOwner extends FinancialAccountsTest {

        @Test
        public void findAccountsByOwner() {

            financialAccounts.findAccountsByOwner(party);

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(FinancialAccount.class));
            assertThat(finderInteraction.getQueryName(), is("findByOwner"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("owner"), is((Object)party));
            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
        }

    }

    public static class AllAccounts extends FinancialAccountsTest {

        @Test
        public void allAccounts() {

            financialAccounts.allAccounts();

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
        }
    }
}
