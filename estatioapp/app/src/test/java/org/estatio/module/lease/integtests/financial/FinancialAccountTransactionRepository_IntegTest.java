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
package org.estatio.module.lease.integtests.financial;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.financial.dom.FinancialAccountRepository;
import org.estatio.module.financial.dom.FinancialAccountTransaction;
import org.estatio.module.financial.dom.FinancialAccountTransactionRepository;
import org.estatio.module.lease.fixtures.bankaccount.personas.BankAccountAndMandateForTopModelGb;
import org.estatio.module.lease.fixtures.bankaccount.personas.FinancialAccountTransactionForTopModel;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class FinancialAccountTransactionRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new BankAccountAndMandateForTopModelGb());
                executionContext.executeChild(this, new FinancialAccountTransactionForTopModel());
            }
        });
    }

    @Inject
    FinancialAccountRepository financialAccountRepository;

    @Inject
    FinancialAccountTransactionRepository financialAccountTransactionRepository;

    @Inject
    PartyRepository partyRepository;

    Party party;

    FinancialAccount financialAccount;

    @Before
    public void setup() throws Exception {
        party = OrganisationAndComms_enum.TopModelGb.findUsing(serviceRegistry);
        List<FinancialAccount> accounts = financialAccountRepository.findAccountsByOwner(party);
        assertThat(accounts.size(), is(1));
        financialAccount = accounts.get(0);
    }

    public static class FindTransaction extends FinancialAccountTransactionRepository_IntegTest {

        @Test
        public void findTransaction() throws Exception {
            // when
            FinancialAccountTransaction financialAccountTransaction = financialAccountTransactionRepository.findTransaction(
                    financialAccount,
                    new LocalDate(2014, 7, 1));

            // then
            assertNotNull(financialAccountTransaction);
            assertThat(financialAccountTransaction.getAmount(), is(new BigDecimal(1000).setScale(2, 0)));
        }
    }

    public static class Transactions extends FinancialAccountTransactionRepository_IntegTest {

        @Test
        public void transactions() throws Exception {
            // when
            List<FinancialAccountTransaction> transactions = financialAccountTransactionRepository.transactions(financialAccount);

            // then
            assertThat(transactions.size(), is(2));
            assertThat(transactions.get(0).getAmount(), is(new BigDecimal(1000).setScale(2, 0)));
            assertThat(transactions.get(1).getAmount(), is(new BigDecimal(2000).setScale(2, 0)));
        }
    }

}