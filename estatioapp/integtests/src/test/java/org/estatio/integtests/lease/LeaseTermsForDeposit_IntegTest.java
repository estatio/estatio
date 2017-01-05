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
package org.estatio.integtests.lease;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceRepository;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.LeaseTermForDeposit;
import org.estatio.dom.leaseinvoicing.InvoiceCalculationSelection;
import org.estatio.dom.leaseinvoicing.InvoiceRunType;
import org.estatio.app.menus.invoice.InvoiceServiceMenuAndContributions;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.integtests.EstatioIntegrationTest;

public class LeaseTermsForDeposit_IntegTest extends EstatioIntegrationTest {

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    InvoiceServiceMenuAndContributions invoiceService;

    @Inject
    InvoiceRepository invoiceRepository;

    public static class LeaseTermForDepositForOxfScenario extends LeaseTermsForDeposit_IntegTest {

        LeaseTermForDeposit depositTerm;
        Lease topmodelLease;
        LocalDate startDate;

        @Before
        public void setUp() throws Exception {

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfTopModel001());
                }
            });
        }

        @Test
        public void invoiceScenarioTest() throws Exception {

            // given
            startDate = new LocalDate(2010, 10, 1);
            topmodelLease = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);

            // when
            invoiceService.calculate(
                    topmodelLease,
                    InvoiceRunType.NORMAL_RUN,
                    InvoiceCalculationSelection.ONLY_DEPOSIT,
                    startDate, startDate, startDate.plusDays(1));

            // then
            Assertions.assertThat(invoiceRepository.findByLease(topmodelLease).size()).isEqualTo(1);
            Assertions.assertThat(invoiceRepository.findByLease(topmodelLease).get(0).getNetAmount()).isEqualTo(new BigDecimal("10000.00"));

            // and when (after couple of indexations of rent items)
            invoiceService.calculate(
                    topmodelLease,
                    InvoiceRunType.NORMAL_RUN,
                    InvoiceCalculationSelection.ONLY_DEPOSIT,
                    startDate.plusYears(5), startDate.plusYears(5), startDate.plusYears(5).plusDays(1));

            // then
            Assertions.assertThat(invoiceRepository.findByLease(topmodelLease).size()).isEqualTo(2);
            Assertions.assertThat(invoiceRepository.findByLease(topmodelLease).get(0).getNetAmount()).isEqualTo(new BigDecimal("10000.00"));
            Assertions.assertThat(invoiceRepository.findByLease(topmodelLease).get(1).getNetAmount()).isEqualTo(new BigDecimal("652.51"));

            // and after approval of first invoice only the delta is invoiced
            final Invoice invoice = invoiceRepository.findByLease(topmodelLease).get(0);
            mixin(Invoice._approve.class, invoice).$$();

            invoiceService.calculate(
                    topmodelLease,
                    InvoiceRunType.NORMAL_RUN,
                    InvoiceCalculationSelection.ONLY_DEPOSIT,
                    startDate.plusYears(5), startDate.plusYears(5), startDate.plusYears(5).plusDays(1));

            // then
            Assertions.assertThat(invoiceRepository.findByLease(topmodelLease).size()).isEqualTo(2);
            Assertions.assertThat(invoiceRepository.findByLease(topmodelLease).get(1).getNetAmount()).isEqualTo(new BigDecimal("652.51"));

            // and after terminating the invoiced deposit is credited
            depositTerm = (LeaseTermForDeposit) topmodelLease.findFirstItemOfType(LeaseItemType.DEPOSIT).getTerms().first();
            depositTerm.terminate(startDate.plusYears(5).minusDays(1));
            final Invoice invoice1 = invoiceRepository.findByLease(topmodelLease).get(1);
            mixin(Invoice._approve.class, invoice1).$$();

            invoiceService.calculate(
                    topmodelLease,
                    InvoiceRunType.RETRO_RUN,
                    InvoiceCalculationSelection.ONLY_DEPOSIT,
                    startDate.plusYears(5), startDate.plusYears(5), startDate.plusYears(5).plusDays(1));

            //then
            Assertions.assertThat(invoiceRepository.findByLease(topmodelLease).size()).isEqualTo(3);
            Assertions.assertThat(invoiceRepository.findByLease(topmodelLease).get(2).getNetAmount()).isEqualTo(new BigDecimal("-10652.51"));

        }

    }

}