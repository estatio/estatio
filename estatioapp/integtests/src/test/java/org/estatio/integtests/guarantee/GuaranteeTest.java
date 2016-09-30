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

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.DisabledException;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.estatio.app.menus.lease.LeaseMenu;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.guarantee.Guarantee;
import org.estatio.dom.guarantee.GuaranteeRepository;
import org.estatio.dom.guarantee.GuaranteeType;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.guarantee.GuaranteeForOxfTopModel001Gb;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;

import static org.assertj.core.api.Assertions.assertThat;

public class GuaranteeTest extends EstatioIntegrationTest {

    @Inject
    LeaseMenu leaseMenu;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    GuaranteeRepository guaranteeRepository;

    @Inject
    TransactionService transactionService;

    Lease lease;

    Guarantee guaranteeWithFinancialAccount;

    Guarantee guaranteeWithoutFinancialAccount;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new GuaranteeForOxfTopModel001Gb());
            }
        }.withTracing());

    }

    @Before
    public void setUp() {
        lease = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
        guaranteeWithFinancialAccount = guaranteeRepository.findByReference(LeaseForOxfTopModel001Gb.REF + "-D");
        GuaranteeType guaranteeType = GuaranteeType.UNKNOWN;
        guaranteeWithoutFinancialAccount = guaranteeRepository.newGuarantee(
                lease, guaranteeType.name(), guaranteeType.name(), guaranteeType, VT.ld("20120101"), null, "", VT.bd(1000), null);
        transactionService.flushTransaction();
    }

    public static class ChangeGuaranteeType extends GuaranteeTest {

        @Test
        public void happyCase1() throws Exception {
            // when
            guaranteeWithoutFinancialAccount.changeGuaranteeType(GuaranteeType.COMPANY_GUARANTEE);

            // then
            assertThat(guaranteeWithoutFinancialAccount.getGuaranteeType()).isEqualTo(GuaranteeType.COMPANY_GUARANTEE);
        }

        @Test
        public void happyCase2() throws Exception {
            // when
            guaranteeWithoutFinancialAccount.changeGuaranteeType(GuaranteeType.BANK_GUARANTEE);
            transactionService.flushTransaction();

            FinancialAccount financialAccount = guaranteeWithoutFinancialAccount.getFinancialAccount();
            Party secondaryParty = lease.getSecondaryParty();

            // then
            assertThat(guaranteeWithoutFinancialAccount.getGuaranteeType()).isEqualTo(GuaranteeType.BANK_GUARANTEE);
            assertThat(financialAccount).isNotNull();
            assertThat(financialAccount.getReference()).isEqualTo(guaranteeWithoutFinancialAccount.getReference());
            assertThat(financialAccount.getOwner()).isEqualTo(secondaryParty);
        }

        @Test
        public void sadCase() throws Exception {
            // when
            try {
                wrap(guaranteeWithFinancialAccount).changeGuaranteeType(GuaranteeType.UNKNOWN);
            } catch (DisabledException e) {
                // TODO: is this the right way to test disabledXxx() testing?
            }

            // then
            assertThat(guaranteeWithFinancialAccount.getGuaranteeType()).isEqualTo(GuaranteeType.BANK_GUARANTEE);
        }
    }

    public static class Terminate extends GuaranteeTest {

        @Test
        public void happyCaseWithFinancialAccount() throws Exception {
            // given
            assertThat(guaranteeWithFinancialAccount.getTerminationDate()).isNull();

            // when
            wrap(guaranteeWithFinancialAccount).terminate(new LocalDate(2016, 1, 1), "Test");

            // then
            assertThat(guaranteeWithFinancialAccount.getTerminationDate()).isEqualTo(new LocalDate(2016, 1, 1));
        }

        @Test
        public void happyCaseWithoutFinancialAccount() throws Exception {
            // given
            assertThat(guaranteeWithoutFinancialAccount.getTerminationDate()).isNull();
            assertThat(guaranteeWithoutFinancialAccount.getFinancialAccount()).isNull();

            // when
            wrap(guaranteeWithoutFinancialAccount).terminate(new LocalDate(2016, 1, 1), "Test");

            // then
            assertThat(guaranteeWithoutFinancialAccount.getTerminationDate()).isEqualTo(new LocalDate(2016, 1, 1));
        }
    }
}