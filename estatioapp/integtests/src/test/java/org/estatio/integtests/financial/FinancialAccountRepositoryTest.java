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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.HiddenException;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccountRepository;
import org.estatio.dom.financial.FinancialAccountType;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.contributed.Party_financialAccountContributions;
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.financial.BankAccountAndMandateForTopModelGb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class FinancialAccountRepositoryTest extends EstatioIntegrationTest {

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
    FinancialAccountRepository financialAccountRepository;

    @Inject
    PartyRepository partyRepository;

    Party party;

    @Before
    public void setup() throws Exception {
        party = partyRepository.findPartyByReference(OrganisationForTopModelGb.REF);
    }

    public static class FindAccountByReference extends FinancialAccountRepositoryTest {

        @Test
        public void forAccount() {
            // when
            FinancialAccount account = financialAccountRepository.findByOwnerAndReference(party, BankAccountAndMandateForTopModelGb.REF);

            // then
            assertThat(account, is(notNullValue()));
            Assert.assertThat(account instanceof BankAccount, is(true));
            final BankAccount bankAccount = (BankAccount) account;
        }

    }

    public static class FindAccountsByOwner extends FinancialAccountRepositoryTest {

        @Test
        public void findAccountsByOwner() throws Exception {
            // when
            List<FinancialAccount> accounts = financialAccountRepository.findAccountsByOwner(party);
            assertThat(accounts.size(), is(1));

            // then
            assertThat(accounts.get(0).getReference(), is(BankAccountAndMandateForTopModelGb.REF));

        }
    }

    public static class FindAccountsByTypeOwner extends FinancialAccountRepositoryTest {

        @Test
        public void findAccountsByTypeOwner() throws Exception {
            // when
            List<FinancialAccount> accounts = financialAccountRepository.findAccountsByTypeOwner(FinancialAccountType.BANK_ACCOUNT, party);
            assertThat(accounts.size(), is(1));

            // then
            assertThat(accounts.get(0).getReference(), is(BankAccountAndMandateForTopModelGb.REF));
        }
    }

    public static class AddAccount extends FinancialAccountRepositoryTest {

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

    public static class AddAccountWrapped extends FinancialAccountRepositoryTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void addAccountIsHiddenTest() throws Exception {

            // then
            expectedException.expect(HiddenException.class);

            // when
            wrapperFactory.wrap(financialAccountContributions).addAccount(party, FinancialAccountType.BANK_ACCOUNT, "123", "test");

        }

        @Inject
        WrapperFactory wrapperFactory;

        @Inject
        Party_financialAccountContributions financialAccountContributions;

    }

}
