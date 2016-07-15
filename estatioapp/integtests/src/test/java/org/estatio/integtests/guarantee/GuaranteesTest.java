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

import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccountType;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.guarantee.Guarantee;
import org.estatio.dom.guarantee.GuaranteeType;
import org.estatio.dom.guarantee.Guarantees;
import org.estatio.dom.guarantee.contributed.OnLease;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseMenu;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.financial.BankAccountForTopModelGb;
import org.estatio.fixture.guarantee.GuaranteeForOxfTopModel001Gb;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class GuaranteesTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new GuaranteeForOxfTopModel001Gb());
                executionContext.executeChild(this, new BankAccountForTopModelGb());
            }
        });

    }

    @Inject
    LeaseMenu leaseMenu;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    Guarantees guarantees;

    @Inject
    OnLease onLease;

    @Inject
    Parties parties;

    @Inject
    FinancialAccounts financialAccounts;

    public static class NewGuarantee extends GuaranteesTest {

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

            assertThat(lease.getPrimaryParty(), is(not(nullValue())));
            assertThat(lease.getSecondaryParty(), is(not(nullValue())));

            startDate = lease.getStartDate();
            endDate = startDate.plusYears(1);
            description = "some description";
            maximumAmount = VT.bd(12300.12);
        }

        @Test
        public void whenBankGuarantee() throws Exception {

            guaranteeType = GuaranteeType.BANK_GUARANTEE;

            // when
            guarantee = guarantees.newGuarantee(lease, guaranteeType.name(), guaranteeType.name(), guaranteeType, startDate, endDate, description, maximumAmount, null);

            // then
            assertThat(guarantee.getLease(), is(lease));
            assertThat(guarantee.getName(), is(guaranteeType.name()));
            assertThat(guarantee.getDescription(), is(description));

            assertThat(guarantee.getStartDate(), is(startDate));
            assertThat(guarantee.getEndDate(), is(endDate));
            assertThat(guarantee.getTerminationDate(), is(nullValue()));

            assertThat(guarantee.getPrimaryParty(), is(lease.getPrimaryParty()));
            assertThat(guarantee.getSecondaryParty(), is(lease.getSecondaryParty()));

            assertThat(guarantee.getContractualAmount(), is(maximumAmount));

            FinancialAccount financialAccount = guarantee.getFinancialAccount();
            assertThat(financialAccount, is(not(nullValue())));

            // and then
            assertThat(financialAccount.getType(), is(FinancialAccountType.BANK_GUARANTEE));
            assertThat(financialAccount.getOwner(), is(lease.getSecondaryParty()));
            assertThat(financialAccount.getName(), is(guaranteeType.name()));
            assertThat(financialAccount.getReference(), is(guaranteeType.name()));
            assertThat(financialAccount.getExternalReference(), is(nullValue()));
        }

        @Test
        public void whenDeposit() throws Exception {

            guaranteeType = GuaranteeType.DEPOSIT;

            // when
            guarantee = guarantees.newGuarantee(lease, guaranteeType.name(), guaranteeType.name(), guaranteeType, startDate, endDate, description, maximumAmount, null);

            // then
            FinancialAccount financialAccount = guarantee.getFinancialAccount();
            assertThat(financialAccount, is(not(nullValue())));

            // and then
            assertThat(financialAccount.getType(), is(FinancialAccountType.GUARANTEE_DEPOSIT));
        }

        @Test
        public void whenCompanyGuarantee() throws Exception {

            guaranteeType = GuaranteeType.COMPANY_GUARANTEE;

            // when
            guarantee = guarantees.newGuarantee(lease, guaranteeType.name(), guaranteeType.name(), guaranteeType, startDate, endDate, description, maximumAmount, null);

            // then
            FinancialAccount financialAccount = guarantee.getFinancialAccount();
            assertThat(financialAccount, is(nullValue()));
        }

        @Test
        public void whenNone() throws Exception {

            guaranteeType = GuaranteeType.NONE;

            // when
            guarantee = guarantees.newGuarantee(lease, guaranteeType.name(), guaranteeType.name(), guaranteeType, startDate, endDate, description, maximumAmount, null);

            // then
            FinancialAccount financialAccount = guarantee.getFinancialAccount();
            assertThat(financialAccount, is(nullValue()));
        }

        @Test
        public void whenUnknown() throws Exception {

            guaranteeType = GuaranteeType.UNKNOWN;

            // when
            guarantee = guarantees.newGuarantee(lease, guaranteeType.name(), guaranteeType.name(), guaranteeType, startDate, endDate, description, maximumAmount, null);

            // then
            FinancialAccount financialAccount = guarantee.getFinancialAccount();
            assertThat(financialAccount, is(nullValue()));
        }
    }

    public static class FindGuarantees extends GuaranteesTest {

        @Test
        public void findGuarantees() throws Exception {

            // when
            List<Guarantee> results = guarantees.findGuarantees(LeaseForOxfTopModel001Gb.REF + "*");

            // then
            assertThat(results.size(), is(1));
        }

        @Test
        public void findGuaranteesByCommentAsWell() throws Exception {

            // given
            Guarantee guarantee = guarantees.findGuarantees(LeaseForOxfTopModel001Gb.REF + "*").get(0);

            // when
            guarantee.setComments("My special comment");

            // then
            assertThat(guarantees.findGuarantees("My special" + "*").size(), is(1));
        }
    }

    public static class FindByReference extends GuaranteesTest {

        @Test
        public void findByReference() throws Exception {
            // when
            Guarantee guarantee = guarantees.findByReference(LeaseForOxfTopModel001Gb.REF + "-D");

            // then
            assertThat(guarantee.getReference(), is(LeaseForOxfTopModel001Gb.REF + "-D"));
        }
    }

    public static class GuaranteesFinder extends GuaranteesTest {

        @Test
        public void guarantees() throws Exception {
            // given
            Lease lease = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);

            // when
            List<Guarantee> results = onLease.guarantees(lease);

            // then
            assertThat(results.size(), is(1));
        }
    }

    public static class FindByFinancialAccount extends GuaranteesTest {

        @Test
        public void happy_case() throws Exception {
            // given
            Party owner = parties.findPartyByReference(LeaseForOxfTopModel001Gb.PARTY_REF_TENANT);
            FinancialAccount account = financialAccounts.findByOwnerAndReference(owner, LeaseForOxfTopModel001Gb.REF + "-D");

            // when
            Guarantee guarantee = guarantees.findbyFinancialAccount(account);

            // then
            assertThat(guarantee.getReference(), is(LeaseForOxfTopModel001Gb.REF + "-D"));
        }
    }
}
