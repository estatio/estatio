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
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BankAccountRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    public static class FindBankMandateRepositoryFor extends BankAccountRepository_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.toFixtureScript());
                    executionContext.executeChild(this, BankAccount_enum.TopModelGb.toFixtureScript());
                }
            });
        }

        @Inject
        private BankAccountRepository bankAccountRepository;

        @Test
        public void autoComplete_works() {
            // given
            List<BankAccount> bankAccountsFound;

            // when
            bankAccountsFound = bankAccountRepository.autoComplete("ABN");
            // then
            assertThat(bankAccountsFound.size()).isEqualTo(1);

            // and when
            bankAccountsFound = bankAccountRepository.autoComplete("***");
            // then
            assertThat(bankAccountsFound.size()).isEqualTo(1);

            // and when
            bankAccountsFound = bankAccountRepository.autoComplete("ACN");
            // then
            assertThat(bankAccountsFound.size()).isEqualTo(0);
        }

    }
}