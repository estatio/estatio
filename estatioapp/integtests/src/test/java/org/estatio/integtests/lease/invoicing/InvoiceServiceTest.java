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
package org.estatio.integtests.lease.invoicing;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.inject.Inject;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexValues;
import org.estatio.dom.index.Indices;
import org.estatio.dom.invoice.CollectionNumerators;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTermForIndexable;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.invoicing.InvoiceCalculationSelection;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoiceItemsForLease;
import org.estatio.dom.lease.invoicing.InvoiceRunType;
import org.estatio.dom.lease.invoicing.InvoiceService;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKalNl;
import org.estatio.fixture.asset._PropertyForOxfGb;
import org.estatio.fixture.index.IndexRefData;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfMediax002Gb;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfPoison003Gb;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfTopModel001;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfMiracl005Gb;
import org.estatio.fixture.lease._LeaseForOxfPret004Gb;
import org.estatio.fixture.party.PersonForJohnDoeNl;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class InvoiceServiceTest extends EstatioIntegrationTest {

    @Inject
    Leases leases;

    @Inject
    InvoiceService invoiceService;

    private static final LocalDate START_DATE = VT.ld(2013, 11, 7);

    @Inject
    Invoices invoices;
    @Inject
    CollectionNumerators collectionNumerators;
    @Inject
    Indices indices;
    @Inject
    IndexValues indexValues;
    @Inject
    InvoiceItemsForLease invoiceItemsForLease;

    Lease lease;
    LeaseItem rItem;
    LeaseItem sItem;
    LeaseItem tItem;


    public static class Lifecycle extends InvoiceServiceTest {

        @Before
        public void setupTransactionalData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new PersonForJohnDoeNl());
                    executionContext.executeChild(this, new _PropertyForOxfGb());
                    executionContext.executeChild(this, new PropertyForKalNl());
                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfTopModel001());
                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfMediax002Gb());
                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfPoison003Gb());
                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003());
                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001());
                    executionContext.executeChild(this, new _LeaseForOxfPret004Gb());
                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfMiracl005Gb());
                }
            });

            lease = leases.findLeaseByReference("OXF-MIRACL-005");
            rItem = lease.findFirstItemOfType(LeaseItemType.RENT);
            sItem = lease.findFirstItemOfType(LeaseItemType.SERVICE_CHARGE);
            tItem = lease.findFirstItemOfType(LeaseItemType.TURNOVER_RENT);
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

            final LeaseTermForIndexable last = (LeaseTermForIndexable) rItem.getTerms().last();
            final LeaseTermForIndexable first = (LeaseTermForIndexable) rItem.getTerms().first();
            assertNotNull(last.getPrevious());
            assertThat(last.getBaseValue(), is(VT.bd(150000).setScale(2)));
            assertThat(first.getStartDate(), is(VT.ld(2013, 11, 7)));
            assertThat(last.getStartDate(), is(VT.ld(2015, 1, 1)));
            assertThat(invoices.findInvoices(lease).size(), is(0));
        }

        public void step2_calculate() throws Exception {
            assertThat("Before calculation", rItem.getTerms().size(), is(2));
            invoiceService.calculate(
                    lease, InvoiceRunType.NORMAL_RUN,
                    InvoiceCalculationSelection.RENT_AND_SERVICE_CHARGE,
                    START_DATE,
                    VT.ld(2013, 10, 1),
                    VT.ld(2015, 4, 1));
            approveInvoicesFor(lease);
            assertThat(totalApprovedOrInvoicedForItem(rItem), is(VT.bd("209918.48")));
            assertThat(totalApprovedOrInvoicedForItem(sItem), is(VT.bd("18103.26")));
            assertThat(invoices.findInvoices(lease).size(), is(1));
        }

        public void step3_approveInvoice() throws Exception {
            collectionNumerators.createInvoiceNumberNumerator(lease.getProperty(), "OXF-%06d", BigInteger.ZERO);
            final List<Invoice> allInvoices = invoices.allInvoices();
            final Invoice invoice = allInvoices.get(allInvoices.size() - 1);
            invoice.approve();
            invoice.doInvoice(VT.ld(2013, 11, 7));
            assertThat(invoice.getInvoiceNumber(), is("OXF-000001"));
            assertThat(invoice.getStatus(), is(InvoiceStatus.INVOICED));
        }

        public void step4_indexation() throws Exception {
            Index index = indices.findIndex("ISTAT-FOI");
            indexValues.newIndexValue(index, VT.ld(2013, 11, 1), VT.bd(110));
            indexValues.newIndexValue(index, VT.ld(2014, 12, 1), VT.bd(115));

            nextTransaction();

            lease.verifyUntil(VT.ld(2015, 3, 31));
            final LeaseTermForIndexable term = (LeaseTermForIndexable) rItem.findTerm(VT.ld(2015, 1, 1));
            assertThat(term.getIndexationPercentage(), is(VT.bd(4.5)));
            assertThat(term.getIndexedValue(), is(VT.bd("156750.00")));
            assertThat(totalApprovedOrInvoicedForItem(rItem), is(VT.bd("209918.48")));
        }

        public void step5_normalInvoice() throws Exception {
            invoiceService.calculate(lease, InvoiceRunType.NORMAL_RUN, InvoiceCalculationSelection.RENT_AND_SERVICE_CHARGE, VT.ld(2015, 4, 1), VT.ld(2015, 4, 1), VT.ld(2015, 4, 1));
            approveInvoicesFor(lease);
            assertThat(totalApprovedOrInvoicedForItem(rItem), is(VT.bd("209918.48")));
        }

        public void step6_retroInvoice() throws Exception {
            invoiceService.calculate(lease, InvoiceRunType.RETRO_RUN, InvoiceCalculationSelection.RENT_AND_SERVICE_CHARGE, VT.ld(2015, 4, 1), VT.ld(2015, 4, 1), VT.ld(2015, 4, 1));
            // (156750 - 150000) / = 1687.5 added
            approveInvoicesFor(lease);
            assertThat(invoices.findInvoices(lease).size(), is(2));
            assertThat(totalApprovedOrInvoicedForItem(rItem), is(VT.bd("209918.48").add(VT.bd("1687.50"))));
        }

        public void step7_terminate() throws Exception {
            lease.terminate(VT.ld(2014, 6, 30), true);
        }

        // //////////////////////////////////////

        private BigDecimal totalApprovedOrInvoicedForItem(final LeaseItem leaseItem) {
            BigDecimal total = BigDecimal.ZERO;
            final InvoiceStatus[] allowed = { InvoiceStatus.APPROVED, InvoiceStatus.INVOICED };
            for (final InvoiceStatus invoiceStatus : allowed) {
                final List<InvoiceItemForLease> items = invoiceItemsForLease.findByLeaseItemAndInvoiceStatus(leaseItem, invoiceStatus);
                for (final InvoiceItemForLease item : items) {
                    total = total.add(item.getNetAmount());
                }
            }
            return total;
        }

        private void approveInvoicesFor(final Lease lease) {
            for (final Invoice invoice : invoices.findInvoices(lease)) {
                invoice.approve();
            }
        }
    }

}