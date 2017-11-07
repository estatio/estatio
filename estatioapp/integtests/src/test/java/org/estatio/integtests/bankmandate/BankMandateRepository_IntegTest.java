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
package org.estatio.integtests.bankmandate;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.bankmandate.BankMandateRepository;
import org.estatio.dom.bankmandate.Scheme;
import org.estatio.dom.bankmandate.SequenceType;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccountRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.financial.BankAccountForTopModelGb;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class BankMandateRepository_IntegTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());

                executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
                executionContext.executeChild(this, new BankAccountForTopModelGb());
            }
        });

        lease = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
    }

    @Inject
    BankMandateRepository bankMandateRepository;

    @Inject
    BankAccountRepository bankAccountRepository;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    TransactionService transactionService;

    Lease lease;

    public static class NewBankMandate extends BankMandateRepository_IntegTest {

        @Test
        public void happy_case() throws Exception {

            // Given
            Party owner = partyRepository.findPartyByReference(LeaseForOxfTopModel001Gb.PARTY_REF_TENANT);
            BankAccount bankAccount = bankAccountRepository.findBankAccountByReference(owner, BankAccountForTopModelGb.REF);

            final String reference = "REF";
            final String name = "NAME";
            final LocalDate startDate = new LocalDate(2013, 1, 1);
            final LocalDate endDate = new LocalDate(2013, 12, 31);
            final Party debtor = lease.getPrimaryParty();
            final Party creditor = lease.getSecondaryParty();
            final SequenceType sequenceType = SequenceType.FIRST;
            final Scheme scheme = Scheme.CORE;
            final LocalDate signatureDate = new LocalDate(2012, 12, 31);

            // When
            BankMandate bankMandate = bankMandateRepository.newBankMandate(reference, name, startDate, endDate, debtor, creditor, bankAccount, sequenceType, scheme, signatureDate);
            transactionService.flushTransaction();

            // Then
            assertThat(bankMandate.getReference()).isEqualTo(reference);
            assertThat(bankMandate.getName()).isEqualTo(name);
            assertThat(bankMandate.getStartDate()).isEqualTo(startDate);
            assertThat(bankMandate.getEndDate()).isEqualTo(endDate);
            assertThat(bankMandate.getPrimaryParty()).isEqualTo(creditor);
            assertThat(bankMandate.getSecondaryParty()).isEqualTo(debtor);
            assertThat(bankMandate.getSequenceType()).isEqualTo(sequenceType);
            assertThat(bankMandate.getScheme()).isEqualTo(scheme);
            assertThat(bankMandate.getSignatureDate()).isEqualTo(signatureDate);

        }

    }

}
