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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.financial.FixedAssetFinancialAccount;
import org.estatio.dom.asset.financial.FixedAssetFinancialAccountRepository;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKalNl;
import org.estatio.fixture.financial.BankAccountForHelloWorldGb;
import org.estatio.fixture.financial.BankAccountForHelloWorldNl;
import org.estatio.fixture.party.OrganisationForHelloWorldGb;
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
                    executionContext.executeChild(this, new BankAccountForHelloWorldGb());
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
            party = parties.findPartyByReference(OrganisationForHelloWorldGb.REF);
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

    public static class RemoveBankAccount extends FinancialAccountTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new BankAccountForHelloWorldNl());
                }
            });
        }

        @Inject
        private FinancialAccounts financialAccounts;

        @Inject
        private FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

        @Inject
        private Parties partyRepository;

        private BankAccount bankAccount;

        private FixedAssetFinancialAccount fixedAssetFinancialAccount;

        private Party owner;

        @Before
        public void setUp() throws Exception {
            owner = partyRepository.findPartyByReference(OrganisationForHelloWorldNl.REF);
            FinancialAccount financialAccount = financialAccounts.findByOwnerAndReference(owner, BankAccountForHelloWorldNl.REF);
            Assert.assertTrue(financialAccount instanceof BankAccount);
            bankAccount = (BankAccount) financialAccount;
        }

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void happyCase() throws Exception {
            // Given
            List<FixedAssetFinancialAccount> results = fixedAssetFinancialAccountRepository.findByFinancialAccount(bankAccount);
            Assert.assertThat(results.size(), is(1));
            fixedAssetFinancialAccount = results.get(0);

            Assert.assertThat(fixedAssetFinancialAccount.getFixedAsset().getReference(), is(PropertyForKalNl.REF));

            // When
            wrap(fixedAssetFinancialAccount).remove();
            Assert.assertThat(fixedAssetFinancialAccountRepository.findByFinancialAccount(bankAccount).size(), is(0));
            wrap(bankAccount).remove();

            // Then
            Assert.assertThat(fixedAssetFinancialAccountRepository.findByFinancialAccount(bankAccount).size(), is(0));
            Assert.assertNull(financialAccounts.findByOwnerAndReference(owner, BankAccountForHelloWorldNl.REF));
        }
    }
}