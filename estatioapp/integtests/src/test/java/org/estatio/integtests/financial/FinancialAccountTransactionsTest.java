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

import java.math.BigDecimal;
import java.util.List;
import javax.inject.Inject;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccountTransaction;
import org.estatio.dom.financial.FinancialAccountTransactions;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.financial.BankAccountAndMandateForTopModelGb;
import org.estatio.fixture.financial.FinancialAccountTransactionForTopModel;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class FinancialAccountTransactionsTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new BankAccountAndMandateForTopModelGb());
                executionContext.executeChild(this, new FinancialAccountTransactionForTopModel());
            }
        });
    }

    @Inject
    FinancialAccounts financialAccounts;

    @Inject
    FinancialAccountTransactions financialAccountTransactions;

    @Inject
    Parties parties;

    Party party;

    FinancialAccount financialAccount;

    @Before
    public void setup() throws Exception {
        party = parties.findPartyByReference(OrganisationForTopModelGb.REF);
        List<FinancialAccount> accounts = financialAccounts.findAccountsByOwner(party);
        assertThat(accounts.size(), is(1));
        financialAccount = accounts.get(0);
    }

    public static class FindTransaction extends FinancialAccountTransactionsTest {

        @Test
        public void findTransaction() throws Exception {
            // when
            FinancialAccountTransaction financialAccountTransaction = financialAccountTransactions.findTransaction(
                    financialAccount,
                    new LocalDate(2014, 7, 1));

            // then
            assertNotNull(financialAccountTransaction);
            assertThat(financialAccountTransaction.getAmount(), is(new BigDecimal(1000).setScale(2, 0)));
        }
    }

    public static class Transactions extends FinancialAccountTransactionsTest {

        @Test
        public void transactions() throws Exception {
            // when
            List<FinancialAccountTransaction> transactions = financialAccountTransactions.transactions(financialAccount);

            // then
            assertThat(transactions.size(), is(2));
            assertThat(transactions.get(0).getAmount(), is(new BigDecimal(1000).setScale(2, 0)));
            assertThat(transactions.get(1).getAmount(), is(new BigDecimal(2000).setScale(2, 0)));
        }
    }

}