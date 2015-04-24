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
package org.estatio.integtests.financial;

import java.util.List;
import javax.inject.Inject;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.financial._BankAccountForHelloWorldNl;
import org.estatio.fixture.party.OrganisationForHelloWorldNl;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;

public class FinancialAccountTest extends EstatioIntegrationTest {

    public static class GetOwner extends FinancialAccountTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new _BankAccountForHelloWorldNl());
                }
            });
        }

        @Inject
        private Parties parties;
        @Inject
        private FinancialAccounts financialAccounts;

        private Party party;

        @Before
        public void setUp() throws Exception {
            party = parties.findPartyByReference(OrganisationForHelloWorldNl.REF);
        }

        // this test really just makes an assertion about the fixture.
        @Test
        public void atLeastOneAccountIsOwnedByParty() throws Exception {

            // given
            List<FinancialAccount> allAccounts = financialAccounts.allAccounts();

            // when
            List<FinancialAccount> partyAccounts = Lists.newArrayList(Iterables.filter(allAccounts, new Predicate<FinancialAccount>() {
                public boolean apply(FinancialAccount fa) {
                    return fa.getOwner() == party;
                }
            }));

            // then
            Assert.assertThat(partyAccounts.size(), is(1));
        }

    }

}