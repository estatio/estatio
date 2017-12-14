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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.assetfinancial.fixtures.bankaccountfafa.enums.BankAccount_enum;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.financial.dom.FinancialAccountRepository;
import org.estatio.module.financial.dom.FinancialAccountType;
import org.estatio.module.lease.fixtures.bankaccount.enums.BankMandate_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class FinancialAccountRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChildren(this, BankMandate_enum.OxfTopModel001Gb_1);
            }
        });
    }

    @Inject
    FinancialAccountRepository financialAccountRepository;

    @Inject
    PartyRepository partyRepository;

    Party party;

    @Before
    public void setup() throws Exception {
        party = OrganisationAndComms_enum.TopModelGb.findUsing(serviceRegistry);
    }

    public static class FindAccountByReference extends FinancialAccountRepository_IntegTest {

        @Test
        public void forAccount() {
            // when
            FinancialAccount account = financialAccountRepository
                    .findByOwnerAndReference(party, BankAccount_enum.TopModelGb.getIban());

            // then
            assertThat(account, is(notNullValue()));
            Assert.assertThat(account instanceof BankAccount, is(true));
            final BankAccount bankAccount = (BankAccount) account;
        }

    }

    public static class FindAccountsByOwner extends FinancialAccountRepository_IntegTest {

        @Test
        public void findAccountsByOwner() throws Exception {
            // when
            List<FinancialAccount> accounts = financialAccountRepository.findAccountsByOwner(party);
            assertThat(accounts.size(), is(1));

            // then
            assertThat(accounts.get(0).getReference(), is(BankAccount_enum.TopModelGb.getIban()));

        }
    }

    public static class FindAccountsByTypeOwner extends FinancialAccountRepository_IntegTest {

        @Test
        public void findAccountsByTypeOwner() throws Exception {
            // when
            List<FinancialAccount> accounts = financialAccountRepository
                    .findAccountsByTypeOwner(FinancialAccountType.BANK_ACCOUNT, party);
            assertThat(accounts.size(), is(1));

            // then
            assertThat(accounts.get(0).getReference(), is(BankAccount_enum.TopModelGb.getIban()));
        }
    }

    public static class AddAccount extends FinancialAccountRepository_IntegTest {

        @Test
        public void addAccountTest() throws Exception {

            // given
            List<FinancialAccount> accounts = financialAccountRepository.findAccountsByOwner(party);
            assertThat(accounts.size(), is(1));

            // when
            financialAccountRepository.newFinancialAccount(FinancialAccountType.BANK_ACCOUNT, "123", "test", party);

            // then
            accounts = financialAccountRepository.findAccountsByOwner(party);
            assertThat(accounts.size(), is(2));

        }

    }

}