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
package org.estatio.integtests.capex.task.invoice;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccountRepository;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.financial.BankAccountForTopModelGb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskForBankAccountVerification_IntegTest extends EstatioIntegrationTest {

    public static class LoadFixtures extends TaskForBankAccountVerification_IntegTest {

        @Before
        public void setupData() {

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new OrganisationForTopModelGb());
                }
            });

        }


        @Inject
        private PartyRepository partyRepository;

        @Inject
        private BankAccountRepository bankAccountRepository;

        @Inject
        private BankAccountVerificationStateTransition.Repository bankAccountVerificationTransitionRepository;

        @Before
        public void setUp() throws Exception {

        }

        @Test
        public void happy_case() throws Exception {

            // given
            final Party seller = partyRepository.findPartyByReference(OrganisationForTopModelGb.REF);

            // When
            BankAccount bankAccount = wrap(bankAccountRepository).newBankAccount(seller, BankAccountForTopModelGb.REF, null);

            // Then
            final List<BankAccountVerificationStateTransition> tasks =  bankAccountVerificationTransitionRepository.findByDomainObject(bankAccount);

            assertThat(tasks.size()).isEqualTo(1);
        }

    }

}