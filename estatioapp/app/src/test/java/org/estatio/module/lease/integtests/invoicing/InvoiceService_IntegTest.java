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
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.base.integtests.VT;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.index.dom.Index;
import org.estatio.module.index.dom.IndexRepository;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceRunType;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.lease.app.InvoiceMenu;
import org.estatio.module.lease.app.InvoiceServiceMenu;
import org.estatio.module.lease.app.LeaseMenu;
import org.estatio.module.lease.app.NumeratorForCollectionMenu;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationSelection;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLeaseRepository;
import org.estatio.module.lease.fixtures.breakoptions.personas.LeaseBreakOptionsForOxfMediax002Gb;
import org.estatio.module.lease.fixtures.breakoptions.personas.LeaseBreakOptionsForOxfPoison003Gb;
import org.estatio.module.lease.fixtures.breakoptions.personas.LeaseBreakOptionsForOxfTopModel001;
import org.estatio.module.lease.fixtures.invoicing.personas.InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001;
import org.estatio.module.lease.fixtures.invoicing.personas.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.deposits.personas.LeaseItemAndLeaseTermForDepositForOxfMiracl005Gb;
import org.estatio.module.lease.fixtures.leaseitems.discount.personas.LeaseItemAndLeaseTermForDiscountForOxfMiracl005Gb;
import org.estatio.module.lease.fixtures.leaseitems.percentage.personas.LeaseItemAndLeaseTermForPercentageForOxfMiracl005Gb;
import org.estatio.module.lease.fixtures.leaseitems.rent.personas.LeaseItemAndLeaseTermForRentOf2ForOxfMiracl005Gb;
import org.estatio.module.lease.fixtures.leaseitems.servicecharge.personas.LeaseItemAndLeaseTermForServiceChargeOf2ForOxfMiracl005Gb;
import org.estatio.module.lease.fixtures.leaseitems.turnoverrent.personas.LeaseItemAndLeaseTermForTurnoverRentForOxfMiracl005Gb;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.lease.migrations.CreateInvoiceNumerators;

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
    LeaseItem rfItem;

    public static class Lifecycle extends InvoiceService_IntegTest {

        @Before
        public void setupTransactionalData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, Person_enum.JohnDoeNl.toBuilderScript());
                    executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.toBuilderScript());
                    executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.KalNl.toBuilderScript());
                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfTopModel001());
                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfMediax002Gb());
                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfPoison003Gb());
                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003());
                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001());
                    executionContext.executeChild(this, Lease_enum.OxfPret004Gb.toBuilderScript());

                    executionContext.executeChild(this, new LeaseItemAndLeaseTermForRentOf2ForOxfMiracl005Gb());
                    executionContext.executeChild(this, new LeaseItemAndLeaseTermForServiceChargeOf2ForOxfMiracl005Gb());
                    executionContext.executeChild(this, new LeaseItemAndLeaseTermForTurnoverRentForOxfMiracl005Gb());
                    executionContext.executeChild(this, new LeaseItemAndLeaseTermForDiscountForOxfMiracl005Gb());
                    executionContext.executeChild(this, new LeaseItemAndLeaseTermForPercentageForOxfMiracl005Gb());
                    executionContext.executeChild(this, new LeaseItemAndLeaseTermForDepositForOxfMiracl005Gb());

                    executionContext.executeChild(this, new CreateInvoiceNumerators());
                }
            });

            lease = leaseRepository.findLeaseByReference("OXF-MIRACL-005");
            rItem = lease.findFirstItemOfType(LeaseItemType.RENT);
            sItem = lease.findFirstItemOfType(LeaseItemType.SERVICE_CHARGE);
            tItem = lease.findFirstItemOfType(LeaseItemType.TURNOVER_RENT);
            dItem = lease.findFirstItemOfType(LeaseItemType.DEPOSIT);
            rfItem = lease.findFirstItemOfType(LeaseItemType.RENTAL_FEE);
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
            assertThat(rfItem.getTerms().size(), is(1));

            final LeaseTermForIndexable last = (LeaseTermForIndexable) rItem.getTerms().last();
            final LeaseTermForIndexable first = (LeaseTermForIndexable) rItem.getTerms().first();
            assertNotNull(last.getPrevious());
            assertThat(last.getBaseValue(), is(VT.bd(150000).setScale(2)));
            assertThat(first.getStartDate(), is(VT.ld(2013, 11, 7)));
            assertThat(last.getStartDate(), is(VT.ld(2015, 1, 1)));
            assertThat(invoiceForLeaseRepository.findByLease(lease).size(), is(0));
            assertThat(dItem.getTerms().last().getEffectiveValue(), is(VT.bd(75000).setScale(2)));
            assertThat(rfItem.getTerms().last().getEffectiveValue(), is(VT.bd(2250).setScale(2)));
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

}