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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import java.util.List;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccountType;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.financial.BankAccountAndMandateForTopModelGb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.integtests.EstatioIntegrationTest;

public class FinancialAccountsTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new BankAccountAndMandateForTopModelGb());
            }
        });
    }

    @Inject
    FinancialAccounts financialAccounts;

    @Inject
    Parties parties;

    Party party;

    @Before
    public void setup() throws Exception {
        party = parties.findPartyByReference(OrganisationForTopModelGb.REF);
    }

    public static class FindAccountByReference extends FinancialAccountsTest {

        @Test
        public void forAccount() {
            // when
            FinancialAccount account = financialAccounts.findAccountByReference(BankAccountAndMandateForTopModelGb.REF);
            // then
            assertThat(account, is(notNullValue()));
            Assert.assertThat(account instanceof BankAccount, is(true));
            final BankAccount bankAccount = (BankAccount) account;
        }

    }

    public static class FindAccountsByOwner extends FinancialAccountsTest {

        @Test
        public void findAccountsByOwner() throws Exception {
            // when
            List<FinancialAccount> accounts = financialAccounts.findAccountsByOwner(party);
            assertThat(accounts.size(), is(1));

            // then
            assertThat(accounts.get(0).getReference(), is(BankAccountAndMandateForTopModelGb.REF));
            
        }
    }

    public static class FindAccountsByTypeOwner extends FinancialAccountsTest {

        @Test
        public void findAccountsByTypeOwner() throws Exception {
            // when
            List<FinancialAccount> accounts = financialAccounts.findAccountsByTypeOwner(FinancialAccountType.BANK_ACCOUNT, party);
            assertThat(accounts.size(), is(1));

            // then
            assertThat(accounts.get(0).getReference(), is(BankAccountAndMandateForTopModelGb.REF));
        }
    }
}
