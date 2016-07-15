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
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.bankmandate.BankMandateMenu;
import org.estatio.dom.bankmandate.BankMandateRepository;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.financial.BankAccountAndMandateForPoisonNl;
import org.estatio.fixture.financial.BankAccountAndMandateForTopModelGb;
import org.estatio.fixture.financial.BankAccountForPoisonNl;
import org.estatio.fixture.lease.LeaseForKalPoison001Nl;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BankMandateMenuTest extends EstatioIntegrationTest {

    public static class FindBankMandateMenuFor extends BankMandateMenuTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new BankAccountAndMandateForTopModelGb());
                    executionContext.executeChild(this, new BankAccountAndMandateForPoisonNl());
                }
            });
        }


        @Inject
        private FinancialAccounts financialAccounts;
        @Inject
        private BankMandateMenu bankMandateMenu;
        @Inject
        private BankMandateRepository bankMandateRepository;
        @Inject
        private Parties parties;

        @Test
        public void forAccountWithMandate() {

            // given
            Party owner = parties.findPartyByReference(LeaseForKalPoison001Nl.PARTY_REF_TENANT);
            FinancialAccount account = financialAccounts.findByOwnerAndReference(owner, BankAccountForPoisonNl.REF);
            Assert.assertThat(account instanceof BankAccount, is(true));
            final BankAccount bankAccount = (BankAccount) account;

            // when
            List<BankMandate> mandates = bankMandateRepository.findBankMandatesFor(bankAccount);

            // then
            assertThat(mandates.size(), is(1));
        }

    }
}