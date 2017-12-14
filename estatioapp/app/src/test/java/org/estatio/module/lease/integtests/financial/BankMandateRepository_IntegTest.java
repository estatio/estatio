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

import org.estatio.module.assetfinancial.fixtures.bankaccountfafa.enums.BankAccount_enum;
import org.estatio.module.bankmandate.dom.BankMandate;
import org.estatio.module.bankmandate.dom.BankMandateRepository;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.financial.dom.FinancialAccountRepository;
import org.estatio.module.lease.fixtures.bankaccount.enums.BankMandate_enum;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.party.dom.PartyRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BankMandateRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    public static class FindBankMandateRepositoryFor extends BankMandateRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {

                    executionContext.executeChildren(this,
                            BankMandate_enum.OxfTopModel001Gb_1,
                            Lease_enum.KalPoison001Nl,
                            BankMandate_enum.KalPoison001Nl_2);
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
            FinancialAccount account = BankAccount_enum.PoisonNl.findUsing(serviceRegistry);
            final BankAccount bankAccount = (BankAccount) account;

            // when
            List<BankMandate> mandates = bankMandateRepository.findBankMandatesFor(bankAccount);

            // then
            assertThat(mandates.size(), is(1));
        }

    }
}