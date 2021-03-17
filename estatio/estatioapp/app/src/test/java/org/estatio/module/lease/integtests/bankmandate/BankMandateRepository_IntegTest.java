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
package org.estatio.module.lease.integtests.bankmandate;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.bankmandate.dom.BankMandate;
import org.estatio.module.bankmandate.dom.BankMandateRepository;
import org.estatio.module.bankmandate.dom.Scheme;
import org.estatio.module.bankmandate.dom.SequenceType;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

public class BankMandateRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, BankAccount_enum.TopModelGb.builder());
            }
        });

        lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
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
            Party owner = OrganisationAndComms_enum.TopModelGb.findUsing(serviceRegistry);
            BankAccount bankAccount = BankAccount_enum.TopModelGb.findUsing(serviceRegistry);

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
            Assertions.assertThat(bankMandate.getReference()).isEqualTo(reference);
            Assertions.assertThat(bankMandate.getName()).isEqualTo(name);
            Assertions.assertThat(bankMandate.getStartDate()).isEqualTo(startDate);
            Assertions.assertThat(bankMandate.getEndDate()).isEqualTo(endDate);
            Assertions.assertThat(bankMandate.getPrimaryParty()).isEqualTo(creditor);
            Assertions.assertThat(bankMandate.getSecondaryParty()).isEqualTo(debtor);
            Assertions.assertThat(bankMandate.getSequenceType()).isEqualTo(sequenceType);
            Assertions.assertThat(bankMandate.getScheme()).isEqualTo(scheme);
            Assertions.assertThat(bankMandate.getSignatureDate()).isEqualTo(signatureDate);

        }

    }

}
