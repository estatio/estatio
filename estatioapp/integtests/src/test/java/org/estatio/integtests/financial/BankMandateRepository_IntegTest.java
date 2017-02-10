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

import org.estatio.bankmandate.dom.BankMandate;
import org.estatio.bankmandate.dom.BankMandateRepository;
import org.estatio.financial.dom.FinancialAccount;
import org.estatio.financial.dom.FinancialAccountRepository;
import org.estatio.financial.dom.bankaccount.BankAccount;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.financial.BankAccountAndMandateForPoisonNl;
import org.estatio.fixture.financial.BankAccountAndMandateForTopModelGb;
import org.estatio.fixture.financial.BankAccountForPoisonNl;
import org.estatio.fixture.lease.LeaseForKalPoison001Nl;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BankMandateRepository_IntegTest extends EstatioIntegrationTest {

    public static class FindBankMandateRepositoryFor extends BankMandateRepository_IntegTest {

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
        private FinancialAccountRepository financialAccountRepository;
        @Inject
        private BankMandateRepository bankMandateRepository;
        @Inject
        private PartyRepository partyRepository;

        @Test
        public void forAccountWithMandate() {

            // given
            Party owner = partyRepository.findPartyByReference(LeaseForKalPoison001Nl.PARTY_REF_TENANT);
            FinancialAccount account = financialAccountRepository.findByOwnerAndReference(owner, BankAccountForPoisonNl.REF);

            Assert.assertThat(account instanceof BankAccount, is(true));
            final BankAccount bankAccount = (BankAccount) account;

            // when
            List<BankMandate> mandates = bankMandateRepository.findBankMandatesFor(bankAccount);

            // then
            assertThat(mandates.size(), is(1));
        }

    }
}