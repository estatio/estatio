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
package org.estatio.integtests.guarantee;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.incode.module.base.integtests.VT;

import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.financial.dom.FinancialAccountRepository;
import org.estatio.module.financial.dom.FinancialAccountType;
import org.estatio.module.guarantee.dom.Guarantee;
import org.estatio.module.guarantee.dom.GuaranteeRepository;
import org.estatio.module.guarantee.dom.GuaranteeType;
import org.estatio.module.guarantee.contributions.LeaseGuaranteeService;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.application.fixtures.EstatioBaseLineFixture;
import org.estatio.module.lease.fixtures.bankaccount.personas.BankAccountAndFaFaForTopModelGb;
import org.estatio.module.guarantee.fixtures.personas.GuaranteeForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.lease.LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class GuaranteeRepository_IntegTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new GuaranteeForOxfTopModel001Gb());
                executionContext.executeChild(this, new BankAccountAndFaFaForTopModelGb());
            }
        });

    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    GuaranteeRepository guaranteeRepository;

    @Inject
    LeaseGuaranteeService leaseNewGuaranteeContribution;

    @Inject
    FinancialAccountRepository financialAccountRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    TransactionService transactionService;

    public static class NewGuarantee extends GuaranteeRepository_IntegTest {

        private Lease lease;
        private Guarantee guarantee;

        private GuaranteeType guaranteeType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String description;
        private BigDecimal maximumAmount;

        @Before
        public void setup() {
            lease = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);

            assertThat(lease.getPrimaryParty()).isNotNull();
            assertThat(lease.getSecondaryParty()).isNotNull();

            startDate = lease.getStartDate();
            endDate = startDate.plusYears(1);
            description = "some description";
            maximumAmount = VT.bd(12300.12);
        }

        @Test
        public void whenBankGuarantee() throws Exception {

            guaranteeType = GuaranteeType.BANK_GUARANTEE;

            // when
            guarantee = guaranteeRepository.newGuarantee(lease, guaranteeType.name(), guaranteeType.name(), guaranteeType, startDate, endDate, description, maximumAmount, null);
            transactionService.flushTransaction();

            // then
            assertThat(guarantee.getLease()).isEqualTo(lease);
            assertThat(guarantee.getName()).isEqualTo(guaranteeType.name());
            assertThat(guarantee.getDescription()).isEqualTo(description);

            assertThat(guarantee.getStartDate()).isEqualTo(startDate);
            assertThat(guarantee.getEndDate()).isEqualTo(endDate);
            assertThat(guarantee.getTerminationDate()).isNull();

            assertThat(guarantee.getPrimaryParty()).isEqualTo(lease.getPrimaryParty());
            assertThat(guarantee.getSecondaryParty()).isEqualTo(lease.getSecondaryParty());

            assertThat(guarantee.getContractualAmount()).isEqualTo(maximumAmount);

            FinancialAccount financialAccount = guarantee.getFinancialAccount();
            assertThat(financialAccount).isNotNull();

            // and then
            assertThat(financialAccount.getType()).isEqualTo(FinancialAccountType.BANK_GUARANTEE);
            assertThat(financialAccount.getOwner()).isEqualTo(lease.getSecondaryParty());
            assertThat(financialAccount.getName()).isEqualTo(guaranteeType.name());
            assertThat(financialAccount.getReference()).isEqualTo(guaranteeType.name());
            assertThat(financialAccount.getExternalReference()).isNull();
        }

        @Test
        public void whenDeposit() throws Exception {

            guaranteeType = GuaranteeType.DEPOSIT;

            // when
            guarantee = guaranteeRepository.newGuarantee(lease, guaranteeType.name(), guaranteeType.name(), guaranteeType, startDate, endDate, description, maximumAmount, null);

            // then
            FinancialAccount financialAccount = guarantee.getFinancialAccount();
            assertThat(financialAccount).isNotNull();

            // and then
            assertThat(financialAccount.getType()).isEqualTo(FinancialAccountType.GUARANTEE_DEPOSIT);
        }

        @Test
        public void whenCompanyGuarantee() throws Exception {

            guaranteeType = GuaranteeType.COMPANY_GUARANTEE;

            // when
            guarantee = guaranteeRepository.newGuarantee(lease, guaranteeType.name(), guaranteeType.name(), guaranteeType, startDate, endDate, description, maximumAmount, null);

            // then
            FinancialAccount financialAccount = guarantee.getFinancialAccount();
            assertThat(financialAccount).isNull();
        }

        @Test
        public void whenNone() throws Exception {

            guaranteeType = GuaranteeType.NONE;

            // when
            guarantee = guaranteeRepository.newGuarantee(lease, guaranteeType.name(), guaranteeType.name(), guaranteeType, startDate, endDate, description, maximumAmount, null);

            // then
            FinancialAccount financialAccount = guarantee.getFinancialAccount();
            assertThat(financialAccount).isNull();
        }

        @Test
        public void whenUnknown() throws Exception {

            guaranteeType = GuaranteeType.UNKNOWN;

            // when
            guarantee = guaranteeRepository.newGuarantee(lease, guaranteeType.name(), guaranteeType.name(), guaranteeType, startDate, endDate, description, maximumAmount, null);

            // then
            FinancialAccount financialAccount = guarantee.getFinancialAccount();
            assertThat(financialAccount).isNull();
        }
    }

    public static class FindGuarantees extends GuaranteeRepository_IntegTest {

        @Test
        public void findGuarantees() throws Exception {

            // when
            List<Guarantee> results = guaranteeRepository.findGuarantees(LeaseForOxfTopModel001Gb.REF + "*");

            // then
            assertThat(results.size()).isEqualTo(1);
        }

        @Test
        public void findGuaranteesByCommentAsWell() throws Exception {

            // given
            Guarantee guarantee = guaranteeRepository.findGuarantees(LeaseForOxfTopModel001Gb.REF + "*").get(0);

            // when
            guarantee.setComments("My special comment");

            // then
            assertThat(guaranteeRepository.findGuarantees("My special" + "*").size()).isEqualTo(1);
        }
    }

    public static class FindByReference extends GuaranteeRepository_IntegTest {

        @Test
        public void findByReference() throws Exception {
            // when
            Guarantee guarantee = guaranteeRepository.findByReference(LeaseForOxfTopModel001Gb.REF + "-D");

            // then
            assertThat(guarantee.getReference()).isEqualTo(LeaseForOxfTopModel001Gb.REF + "-D");
        }
    }

    public static class GuaranteesFinder extends GuaranteeRepository_IntegTest {

        @Test
        public void guarantees() throws Exception {
            // given
            Lease lease = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);

            // when
            List<Guarantee> results = leaseNewGuaranteeContribution.guarantees(lease);

            // then
            assertThat(results.size()).isEqualTo(1);
        }
    }

    public static class FindByLease extends GuaranteeRepository_IntegTest {

        @Test
        public void findByLease() throws Exception {
            // given
            Lease lease = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);

            // when
            List<Guarantee> results = guaranteeRepository.findByLease(lease);

            // then
            assertThat(results.size()).isEqualTo(1);
        }
    }

    public static class FindByFinancialAccount extends GuaranteeRepository_IntegTest {

        @Test

        public void happy_case() throws Exception {
            // given
            Party owner = partyRepository.findPartyByReference(LeaseForOxfTopModel001Gb.PARTY_REF_TENANT);
            FinancialAccount account = financialAccountRepository.findByOwnerAndReference(owner, LeaseForOxfTopModel001Gb.REF + "-D");

            // when
            Guarantee guarantee = guaranteeRepository.findbyFinancialAccount(account);

            // then
            assertThat(guarantee.getReference()).isEqualTo(LeaseForOxfTopModel001Gb.REF + "-D");
        }
    }
}
