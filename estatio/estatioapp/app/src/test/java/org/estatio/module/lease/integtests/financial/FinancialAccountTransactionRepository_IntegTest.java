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

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.financial.dom.FinancialAccountRepository;
import org.estatio.module.financial.dom.FinancialAccountTransaction;
import org.estatio.module.financial.dom.FinancialAccountTransactionRepository;
import org.estatio.module.financial.fixtures.fatransaction.enums.FinancialAccountTransaction_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import static org.hamcrest.CoreMatchers.is;
import static org.incode.module.base.integtests.VT.bd;
import static org.incode.module.base.integtests.VT.ld;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class FinancialAccountTransactionRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext ec) {
                ec.executeChildren(this,
                        FinancialAccountTransaction_enum.TopModelGb_xactn1,
                        FinancialAccountTransaction_enum.TopModelGb_xactn2);
            }
        });
    }

    @Inject
    FinancialAccountRepository financialAccountRepository;

    @Inject
    FinancialAccountTransactionRepository financialAccountTransactionRepository;

    Party party;

    FinancialAccount financialAccount;

    @Before
    public void setup() {
        party = OrganisationAndComms_enum.TopModelGb.findUsing(serviceRegistry);
        List<FinancialAccount> accounts = financialAccountRepository.findAccountsByOwner(party);
        assertThat(accounts.size(), is(1));
        financialAccount = accounts.get(0);
    }

    public static class FindTransaction extends FinancialAccountTransactionRepository_IntegTest {

        @Test
        public void findTransaction() {
            // when
            FinancialAccountTransaction financialAccountTransaction = financialAccountTransactionRepository.findTransaction(
                    financialAccount,
                    ld(2014, 7, 1));

            // then
            assertNotNull(financialAccountTransaction);
            assertThat(financialAccountTransaction.getAmount(), is(bd(1000).setScale(2,0)));
        }
    }

    public static class Transactions extends FinancialAccountTransactionRepository_IntegTest {

        @Test
        public void transactions() {
            // when
            List<FinancialAccountTransaction> transactions = financialAccountTransactionRepository.transactions(financialAccount);

            // then
            assertThat(transactions.size(), is(2));
            assertThat(transactions.get(0).getAmount(), is(bd(1000).setScale(2,0)));
            assertThat(transactions.get(1).getAmount(), is(bd(2000).setScale(2,0)));
        }
    }

}