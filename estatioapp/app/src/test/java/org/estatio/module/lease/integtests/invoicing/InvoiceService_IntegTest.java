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
package org.estatio.module.lease.integtests.invoicing;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.base.integtests.VT;
import org.incode.module.docfragment.dom.impl.DocFragmentRepository;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.index.dom.Index;
import org.estatio.module.index.dom.IndexRepository;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceItem;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceRunType;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.lease.app.InvoiceMenu;
import org.estatio.module.lease.app.InvoiceServiceMenu;
import org.estatio.module.lease.app.LeaseMenu;
import org.estatio.module.lease.app.NumeratorForCollectionMenu;
import org.estatio.module.lease.contributions.Lease_calculate;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTermForFixed;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationSelection;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLeaseRepository;
import org.estatio.module.lease.fixtures.breakoptions.enums.BreakOption_enum;
import org.estatio.module.lease.fixtures.invoice.enums.InvoiceForLease_enum;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDeposit_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForEntryFee_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRent_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class InvoiceService_IntegTest extends LeaseModuleIntegTestAbstract {

    private static final LocalDate START_DATE = VT.ld(2013, 11, 7);

    @Inject
    LeaseMenu leaseMenu;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    InvoiceServiceMenu invoiceService;

    @Inject
    InvoiceRepository invoiceRepository;

    @Inject
    InvoiceForLeaseRepository invoiceForLeaseRepository;

    @Inject
    InvoiceMenu invoiceMenu;

    @Inject
    NumeratorForCollectionMenu estatioNumeratorRepository;

    @Inject
    IndexRepository indexRepository;

    @Inject
    InvoiceItemForLeaseRepository invoiceItemForLeaseRepository;

    Lease lease;
    LeaseItem rItem;
    LeaseItem sItem;
    LeaseItem tItem;
    LeaseItem dItem;

    public static class Lifecycle extends InvoiceService_IntegTest {

        @Before
        public void setupTransactionalData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext ec) {

                    ec.executeChildren(this,
                            Person_enum.JohnDoeNl,
                            PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
                            PropertyAndUnitsAndOwnerAndManager_enum.KalNl);

                    ec.executeChildren(this,
                            BreakOption_enum.OxfPoison003Gb_FIXED,
                            BreakOption_enum.OxfPoison003Gb_ROLLING,
                            BreakOption_enum.OxfPoison003Gb_FIXED,
                            BreakOption_enum.OxfPoison003Gb_ROLLING,
                            BreakOption_enum.OxfTopModel001Gb_FIXED,
                            BreakOption_enum.OxfTopModel001Gb_ROLLING);

                    ec.executeChildren(this,
                            InvoiceForLease_enum.OxfPoison003Gb,
                            InvoiceForLease_enum.KalPoison001Nl);

                    ec.executeChildren(this,
                            Lease_enum.OxfPret004Gb,

                            LeaseItemForRent_enum.OxfMiracl005Gb,
                            LeaseItemForServiceCharge_enum.OxfMiracl005Gb,
                            LeaseItemForTurnoverRent_enum.OxfMiracl005Gb,
                            LeaseItemForDiscount_enum.OxfMiracle005bGb,
                            LeaseItemForDeposit_enum.OxfMiracle005bGb);
                }
            });

            lease = leaseRepository.findLeaseByReference("OXF-MIRACL-005");
            rItem = lease.findFirstItemOfType(LeaseItemType.RENT);
            sItem = lease.findFirstItemOfType(LeaseItemType.SERVICE_CHARGE);
            tItem = lease.findFirstItemOfType(LeaseItemType.TURNOVER_RENT);
            dItem = lease.findFirstItemOfType(LeaseItemType.DEPOSIT);
        }

        @Test
        public void fullLifecycle() throws Exception {
            step1_verify();
            step2_calculate();
            step3_approveInvoice();
            step4_indexation();
            step5_normalInvoice();
            step6_retroInvoice();
            step7_terminate();
        }

        public void step1_verify() throws Exception {
            // when
            lease.verifyUntil(VT.ld(2015, 1, 1));
            // then
            assertThat(rItem.getTerms().size(), is(2));
            assertThat(sItem.getTerms().size(), is(2));
            assertThat(tItem.getTerms().size(), is(2));
            assertThat(dItem.getTerms().size(), is(1));

            final LeaseTermForIndexable last = (LeaseTermForIndexable) rItem.getTerms().last();
            final LeaseTermForIndexable first = (LeaseTermForIndexable) rItem.getTerms().first();
            assertNotNull(last.getPrevious());
            assertThat(last.getBaseValue(), is(VT.bd(150000).setScale(2)));
            assertThat(first.getStartDate(), is(VT.ld(2013, 11, 7)));
            assertThat(last.getStartDate(), is(VT.ld(2015, 1, 1)));
            assertThat(invoiceForLeaseRepository.findByLease(lease).size(), is(0));
            assertThat(dItem.getTerms().last().getEffectiveValue(), is(VT.bd(75000).setScale(2)));
        }

        public void step2_calculate() throws Exception {
            assertThat("Before calculation", rItem.getTerms().size(), is(2));
            invoiceService.calculateLegacy(
                    lease, InvoiceRunType.NORMAL_RUN,
                    InvoiceCalculationSelection.ALL_RENT_AND_SERVICE_CHARGE,
                    START_DATE,
                    VT.ld(2013, 10, 1),
                    VT.ld(2015, 4, 1));
            approveInvoicesFor(lease);
            assertThat(totalApprovedOrInvoicedForItem(rItem), is(VT.bd("209918.48")));
            assertThat(totalApprovedOrInvoicedForItem(sItem), is(VT.bd("18103.26")));
            assertThat(invoiceForLeaseRepository.findByLease(lease).size(), is(1));
        }

        public void step3_approveInvoice() throws Exception {
            final List<Invoice> allInvoices = invoiceMenu.allInvoices();
            final Invoice invoice = allInvoices.get(allInvoices.size() - 1);
            //estatioNumeratorRepository.createInvoiceNumberNumerator(lease.getProperty(), "OXF-%06d", BigInteger.ZERO, invoice.getApplicationTenancy());

            mixin(InvoiceForLease._approve.class, invoice).$$();
            mixin(InvoiceForLease._invoice.class, invoice).$$(VT.ld(2013, 11, 7));

            assertThat(invoice.getInvoiceNumber(), is("OXF-0001"));
            assertThat(invoice.getStatus(), is(InvoiceStatus.INVOICED));
        }

        public void step4_indexation() throws Exception {
            Index index = indexRepository.findByReference("ISTAT-FOI");
            index.newIndexValue(VT.ld(2013, 11, 1), VT.bd(110));
            index.newIndexValue(VT.ld(2014, 12, 1), VT.bd(115));

            transactionService.nextTransaction();

            lease.verifyUntil(VT.ld(2015, 3, 31));
            final LeaseTermForIndexable term = (LeaseTermForIndexable) rItem.findTerm(VT.ld(2015, 1, 1));
            assertThat(term.getIndexationPercentage(), is(VT.bd(4.5)));
            assertThat(term.getIndexedValue(), is(VT.bd("156750.00")));
            assertThat(totalApprovedOrInvoicedForItem(rItem), is(VT.bd("209918.48")));
        }

        public void step5_normalInvoice() throws Exception {
            invoiceService.calculateLegacy(lease, InvoiceRunType.NORMAL_RUN, InvoiceCalculationSelection.ALL_RENT_AND_SERVICE_CHARGE, VT.ld(2015, 4, 1), VT.ld(2015, 4, 1), VT.ld(2015, 4, 1));
            approveInvoicesFor(lease);
            assertThat(totalApprovedOrInvoicedForItem(rItem), is(VT.bd("209918.48")));
        }

        public void step6_retroInvoice() throws Exception {
            invoiceService.calculateLegacy(lease, InvoiceRunType.RETRO_RUN, InvoiceCalculationSelection.ALL_RENT_AND_SERVICE_CHARGE, VT.ld(2015, 4, 1), VT.ld(2015, 4, 1), VT.ld(2015, 4, 1));
            // (156750 - 150000) / = 1687.5 added
            approveInvoicesFor(lease);
            assertThat(invoiceForLeaseRepository.findByLease(lease).size(), is(2));
            assertThat(totalApprovedOrInvoicedForItem(rItem), is(VT.bd("209918.48").add(VT.bd("1687.50"))));
        }

        public void step7_terminate() throws Exception {
            lease.terminate(VT.ld(2014, 6, 30));
        }

        // //////////////////////////////////////

        private BigDecimal totalApprovedOrInvoicedForItem(final LeaseItem leaseItem) {
            BigDecimal total = BigDecimal.ZERO;
            final InvoiceStatus[] allowed = { InvoiceStatus.APPROVED, InvoiceStatus.INVOICED };
            for (final InvoiceStatus invoiceStatus : allowed) {
                final List<InvoiceItemForLease> items = invoiceItemForLeaseRepository.findByLeaseItemAndInvoiceStatus(leaseItem, invoiceStatus);
                for (final InvoiceItemForLease item : items) {
                    total = total.add(item.getNetAmount());
                }
            }
            return total;
        }

        private void approveInvoicesFor(final Lease lease) {
            for (final Invoice invoice : invoiceForLeaseRepository.findByLease(lease)) {
                mixin(InvoiceForLease._approve.class, invoice).$$();
            }
        }
    }

    public static class RetroInvoiceForFixedInAdvanceItem extends InvoiceService_IntegTest {

        @Before
        public void setupTransactionalData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext ec) {

                    ec.executeChildren(this,
                            Lease_enum.OxfFix006Gb);
                    ec.executeChildren(this,
                            LeaseItemForRent_enum.OxfFix006Gb);
                    ec.executeChildren(this,
                            LeaseItemForEntryFee_enum.OxfFix006Gb);
                }
            });

        }

        @Test
        public void retro_invoicing_fixed_in_advance_item_works() throws Exception {

            // given
            Lease leaseForFix = Lease_enum.OxfFix006Gb.findUsing(serviceRegistry);
            LeaseItem entryFeeItem = leaseForFix.findItemsOfType(LeaseItemType.ENTRY_FEE).get(0);
            LeaseTermForFixed entryFeeTerm = (LeaseTermForFixed) entryFeeItem.getTerms().first();

            Assertions.assertThat(entryFeeItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.FIXED_IN_ADVANCE);
            Assertions.assertThat(invoiceForLeaseRepository.findByLease(leaseForFix).isEmpty()).isTrue();
            Assertions.assertThat(entryFeeTerm.getValue()).isEqualTo(new BigDecimal("5000.00"));

            // when
            wrap(mixin(Lease_calculate.class, leaseForFix)).exec(
                    InvoiceRunType.RETRO_RUN,
                    Arrays.asList(LeaseItemType.ENTRY_FEE),
                    new LocalDate(2018, 1, 1),
                    new LocalDate(2018, 1, 1),
                    new LocalDate(2018, 1, 2)
                    );

            // then
            Assertions.assertThat(invoiceForLeaseRepository.findByLease(leaseForFix).size()).isEqualTo(1);
            Invoice invoiceForEntryFee = invoiceForLeaseRepository.findByLease(leaseForFix).get(0);
            Assertions.assertThat(invoiceForEntryFee.getTotalNetAmount()).isEqualTo(entryFeeTerm.getValue());

        }

    }

    public static class VatRoundingForItalianInvoices extends InvoiceService_IntegTest {

        @Before
        public void setupTransactionalData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext ec) {

                    ec.executeChildren(this,
                            LeaseItemForRent_enum.RonTopModel001It);

                }
            });
        }

        @Test
        public void italian_invoice_with_rouding_issues_is_corrected_on_invoicing() throws Exception {

            // given - italian lease with rounding problems
            docFragmentRepository.create("org.estatio.dom.lease.invoicing.ssrs.InvoiceAttributesVM", "description", "/ITA/RON/HW_IT", "");
            docFragmentRepository.create("org.estatio.dom.lease.invoicing.ssrs.InvoiceAttributesVM", "preliminaryLetterDescription", "/ITA/RON/HW_IT", "");
            docFragmentRepository.create("org.estatio.dom.lease.invoicing.ssrs.InvoiceItemAttributesVM", "description", "/ITA/RON/HW_IT", "");

            Lease lease = Lease_enum.RonTopModel001It.findUsing(serviceRegistry);
            mixin(Lease_calculate.class, lease).exec(InvoiceRunType.NORMAL_RUN, Arrays.asList(LeaseItemType.RENT), new LocalDate(2019,01,01), new LocalDate(2019,01,01), new LocalDate(2019,1,2));
            InvoiceForLease invoice = invoiceForLeaseRepository.findByLease(lease).get(0);
            InvoiceItem rentItem = invoice.getItems().first();
            Charge charge = rentItem.getCharge();
            rentItem.remove();

            InvoiceItem item = mixin(InvoiceForLease._newItem.class, invoice).$$(charge, new BigDecimal("1"), new BigDecimal("0.08"), null, null);
            Assertions.assertThat(item.getVatAmount()).isEqualTo(new BigDecimal("0.02"));
            Assertions.assertThat(item.getGrossAmount()).isEqualTo(new BigDecimal("0.10"));

            InvoiceItem itemCausingRoundingProblem = mixin(InvoiceForLease._newItem.class, invoice).$$(charge, new BigDecimal("1"), new BigDecimal("0.17"), null, null);
            Assertions.assertThat(itemCausingRoundingProblem.getVatAmount()).isEqualTo(new BigDecimal("0.04"));
            Assertions.assertThat(itemCausingRoundingProblem.getGrossAmount()).isEqualTo(new BigDecimal("0.21"));

            // when
            mixin(InvoiceForLease._approve.class, invoice).$$();
            // then still not corrected
            Assertions.assertThat(invoice.getItems().first().getVatAmount()).isEqualTo("0.04");
            Assertions.assertThat(invoice.getItems().first().getGrossAmount()).isEqualTo("0.21");

            // when
            mixin(InvoiceForLease._collect.class, invoice).$$();
            // then still not corrected
            Assertions.assertThat(invoice.getItems().first().getVatAmount()).isEqualTo("0.04");
            Assertions.assertThat(invoice.getItems().first().getGrossAmount()).isEqualTo("0.21");

            // when
            mixin(InvoiceForLease._invoice.class, invoice).$$(new LocalDate(2019,01,01));
            // then the largest amount is corrected
            Assertions.assertThat(invoice.getItems().first().getVatAmount()).isEqualTo("0.03");
            Assertions.assertThat(invoice.getItems().first().getGrossAmount()).isEqualTo("0.20");

        }

        @Inject InvoiceForLeaseRepository invoiceForLeaseRepository;

        @Inject DocFragmentRepository docFragmentRepository;

    }


}