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
import java.util.Arrays;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.app.menus.invoice.InvoiceServiceMenuAndContributions;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceRunType;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Fraction;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.LeaseTermForDeposit;
import org.estatio.dom.lease.LeaseTermForIndexable;
import org.estatio.dom.lease.invoicing.InvoiceCalculationSelection;
import org.estatio.dom.lease.invoicing.InvoiceForLease;
import org.estatio.dom.lease.invoicing.InvoiceForLeaseRepository;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.fixture.lease.LeaseForOxfMediaX002Gb;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfMediax002Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseTermsForDeposit_IntegTest extends EstatioIntegrationTest {

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    InvoiceServiceMenuAndContributions invoiceService;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    InvoiceForLeaseRepository invoiceForLeaseRepository;

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
            invoiceService.calculateLegacy(
                    topmodelLease,
                    InvoiceRunType.NORMAL_RUN,
                    InvoiceCalculationSelection.ONLY_DEPOSIT,
                    startDate, startDate, startDate.plusDays(1));

            // then
            assertThat(invoiceForLeaseRepository.findByLease(topmodelLease).size()).isEqualTo(1);
            assertThat(invoiceForLeaseRepository.findByLease(topmodelLease).get(0).getNetAmount()).isEqualTo(new BigDecimal("10000.00"));

            // and when (after couple of indexations of rent items)
            invoiceService.calculateLegacy(
                    topmodelLease,
                    InvoiceRunType.NORMAL_RUN,
                    InvoiceCalculationSelection.ONLY_DEPOSIT,
                    startDate.plusYears(5), startDate.plusYears(5), startDate.plusYears(5).plusDays(1));

            // then
            assertThat(invoiceForLeaseRepository.findByLease(topmodelLease).size()).isEqualTo(2);
            assertThat(invoiceForLeaseRepository.findByLease(topmodelLease).get(0).getNetAmount()).isEqualTo(new BigDecimal("10000.00"));
            assertThat(invoiceForLeaseRepository.findByLease(topmodelLease).get(1).getNetAmount()).isEqualTo(new BigDecimal("652.51"));

            // and after approval of first invoice only the delta is invoiced
            final Invoice invoice = invoiceForLeaseRepository.findByLease(topmodelLease).get(0);
            mixin(InvoiceForLease._approve.class, invoice).$$();

            invoiceService.calculateLegacy(
                    topmodelLease,
                    InvoiceRunType.NORMAL_RUN,
                    InvoiceCalculationSelection.ONLY_DEPOSIT,
                    startDate.plusYears(5), startDate.plusYears(5), startDate.plusYears(5).plusDays(1));

            // then
            assertThat(invoiceForLeaseRepository.findByLease(topmodelLease).size()).isEqualTo(2);
            assertThat(invoiceForLeaseRepository.findByLease(topmodelLease).get(1).getNetAmount()).isEqualTo(new BigDecimal("652.51"));

            // and after terminating the invoiced deposit is credited
            depositTerm = (LeaseTermForDeposit) topmodelLease.findFirstItemOfType(LeaseItemType.DEPOSIT).getTerms().first();
            depositTerm.terminate(startDate.plusYears(5).minusDays(1));
            final Invoice invoice1 = invoiceForLeaseRepository.findByLease(topmodelLease).get(1);
            mixin(InvoiceForLease._approve.class, invoice1).$$();

            invoiceService.calculateLegacy(
                    topmodelLease,
                    InvoiceRunType.RETRO_RUN,
                    InvoiceCalculationSelection.ONLY_DEPOSIT,
                    startDate.plusYears(5), startDate.plusYears(5), startDate.plusYears(5).plusDays(1));

            //then
            assertThat(invoiceForLeaseRepository.findByLease(topmodelLease).size()).isEqualTo(3);
            assertThat(invoiceForLeaseRepository.findByLease(topmodelLease).get(2).getNetAmount()).isEqualTo(new BigDecimal("-10652.51"));

        }

    }

    public static class DepositInvoicedInArrearsTest extends LeaseTermsForDeposit_IntegTest {

        Lease leaseForMedia;
        LeaseItem rentItem;
        LocalDate startDateDeposit = new LocalDate(2010,01,01);
        LocalDate invoiceDueDate = new LocalDate(2011, 01,01);

        @Before
        public void setUp() throws Exception {

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new LeaseForOxfMediaX002Gb());
                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfMediax002Gb());
                }
            });

            leaseForMedia = leaseRepository.findLeaseByReference(LeaseForOxfMediaX002Gb.REF);
            rentItem = leaseForMedia.findFirstItemOfType(LeaseItemType.RENT);
            LeaseTermForIndexable firstRentTerm = (LeaseTermForIndexable) rentItem.getTerms().first();
            assertThat(firstRentTerm.getBaseValue()).isEqualTo(new BigDecimal("20000.00"));

        }

        @Test
        public void depositInvoicedInArrearsTest() {

            // given 2 identical deposit items: 1 invoice in arrears, 1 invoiced in advance
            LeaseItem depositItemInArrears = wrap(leaseForMedia).newItem(LeaseItemType.DEPOSIT, LeaseConstants.AgreementRoleType.LANDLORD, chargeRepository.findByReference(ChargeRefData.GB_DEPOSIT), InvoicingFrequency.QUARTERLY_IN_ARREARS, PaymentMethod.DIRECT_DEBIT, startDateDeposit);
            wrap(depositItemInArrears).newSourceItem(rentItem);

            LeaseItem depositItemInAdvance = leaseForMedia.newItem(LeaseItemType.DEPOSIT, LeaseConstants.AgreementRoleType.LANDLORD, chargeRepository.findByReference(ChargeRefData.GB_DEPOSIT), InvoicingFrequency.QUARTERLY_IN_ADVANCE, PaymentMethod.DIRECT_DEBIT, startDateDeposit);
            wrap(depositItemInAdvance).newSourceItem(rentItem);

            LeaseTermForDeposit termInArrears = (LeaseTermForDeposit) wrap(depositItemInArrears).newTerm(startDateDeposit, null);
            termInArrears.setFraction(Fraction.M6);

            LeaseTermForDeposit termInAdvance = (LeaseTermForDeposit) wrap(depositItemInAdvance).newTerm(startDateDeposit, null);
            termInAdvance.setFraction(Fraction.M6);
            transactionService.nextTransaction();

            // when
            getFixtureClock().setDate(2011, 4, 1);
            LocalDate fixtureDate = clockService.now();
            wrap(leaseForMedia).verifyUntil(fixtureDate.plusDays(1));

            // then
            assertThat(rentItem.valueForDate(startDateDeposit)).isEqualTo(new BigDecimal("20563.90"));
            assertThat(rentItem.valueForDate(invoiceDueDate.minusDays(1))).isEqualTo(new BigDecimal("20563.90"));
            assertThat(rentItem.valueForDate(invoiceDueDate)).isEqualTo(new BigDecimal("21016.31"));

            assertThat(depositItemInAdvance.getTerms().size()).isEqualTo(1);
            assertThat(termInAdvance.getDepositBase()).isEqualTo(new BigDecimal("21016.31"));
            assertThat(termInAdvance.valueForDate(startDateDeposit)).isEqualTo(new BigDecimal("10281.95"));
            assertThat(termInAdvance.valueForDate(invoiceDueDate)).isEqualTo(new BigDecimal("10508.16"));

            assertThat(depositItemInArrears.getTerms().size()).isEqualTo(1);
            assertThat(termInArrears.getDepositBase()).isEqualTo(new BigDecimal("21016.31"));
            assertThat(termInArrears.valueForDate(startDateDeposit)).isEqualTo(new BigDecimal("10281.95"));
            assertThat(termInArrears.valueForDate(invoiceDueDate)).isEqualTo(new BigDecimal("10508.16"));

            // and when
            invoiceService.calculate(leaseForMedia, InvoiceRunType.RETRO_RUN, Arrays.asList(LeaseItemType.DEPOSIT),invoiceDueDate,invoiceDueDate,invoiceDueDate.plusDays(1));
            //TODO: Normal run does not work (no intervals are returned invoiceCalculationService#L211 for arrears) with this next due date
            transactionService.nextTransaction();

            // then
            assertThat(invoiceForLeaseRepository.findByLease(leaseForMedia).size()).isEqualTo(1);
            InvoiceForLease invoice = invoiceForLeaseRepository.findByLease(leaseForMedia).get(0);
            assertThat(invoice.getItems().size()).isEqualTo(2);

            InvoiceItemForLease invoiceItemInArrears = (InvoiceItemForLease) invoice.getItems().first();
            assertThat(invoiceItemInArrears.getSource()).isEqualTo(termInArrears);
            assertThat(invoiceItemInArrears.getNetAmount()).isEqualTo(new BigDecimal("10281.95"));

            InvoiceItemForLease invoiceItemInAdvance = (InvoiceItemForLease) invoice.getItems().last();
            assertThat(invoiceItemInAdvance.getSource()).isEqualTo(termInAdvance);
            assertThat(invoiceItemInAdvance.getNetAmount()).isEqualTo(new BigDecimal("10508.16"));

        }

        @Inject ClockService clockService;

    }

}