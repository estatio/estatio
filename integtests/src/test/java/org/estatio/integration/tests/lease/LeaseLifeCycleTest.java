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
package org.estatio.integration.tests.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexValues;
import org.estatio.dom.index.Indices;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForIndexableRent;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.Leases.InvoiceRunType;
import org.estatio.dom.lease.invoicing.InvoiceCalculationSelection;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoiceItemsForLease;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LeaseLifeCycleTest extends EstatioIntegrationTest {

    private static final LocalDate START_DATE = new LocalDate(2013, 11, 7);

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioTransactionalObjectsFixture());
    }

    private Leases leases;
    private Lease lease;
    private LeaseItem rItem;
    private LeaseItem sItem;
    private LeaseItem tItem;
    private Invoices invoices;
    private Indices indices;
    private IndexValues indexValues;
    private InvoiceItemsForLease invoiceItemsForLease;

    @Before
    public void setup() {
        leases = service(Leases.class);
        indices = service(Indices.class);
        invoices = service(Invoices.class);
        invoiceItemsForLease = service(InvoiceItemsForLease.class);
        lease = leases.findLeaseByReference("OXF-MIRACL-005");
        rItem = lease.findFirstItemOfType(LeaseItemType.RENT);
        sItem = lease.findFirstItemOfType(LeaseItemType.SERVICE_CHARGE);
        tItem = lease.findFirstItemOfType(LeaseItemType.TURNOVER_RENT);
        indexValues = service(IndexValues.class);
    }

    @Test
    public void step1_verify() throws Exception {
        // when
        lease.verifyUntil(new LocalDate(2015, 1, 1));
        // then
        assertThat(rItem.getTerms().size(), is(2));
        assertThat(sItem.getTerms().size(), is(3));
        assertThat(tItem.getTerms().size(), is(2));

        LeaseTermForIndexableRent last = (LeaseTermForIndexableRent) rItem.getTerms().last();
        LeaseTermForIndexableRent first = (LeaseTermForIndexableRent) rItem.getTerms().first();
        assertThat(last.getBaseValue(), is(new BigDecimal(150000).setScale(2)));
        assertThat(first.getStartDate(), is(new LocalDate(2013, 11, 7)));
        assertThat(last.getStartDate(), is(new LocalDate(2015, 1, 1)));
        assertThat(invoices.findInvoices(lease).size(), is(0));
    }

    @Test
    public void step2_caluclate() throws Exception {
        assertThat("Before calculation", rItem.getTerms().size(), is(2));
        lease.calculate(
                InvoiceRunType.NORMAL_RUN,
                InvoiceCalculationSelection.RENT_AND_SERVICE_CHARGE,
                START_DATE,
                new LocalDate(2013, 10, 1),
                new LocalDate(2015, 4, 1));
        approveInvoices();
        assertThat(totalApporvedOrInvoicedForItem(rItem), is(new BigDecimal("209918.48")));
        assertThat(totalApporvedOrInvoicedForItem(sItem), is(new BigDecimal("18103.26")));
        assertThat(invoices.findInvoices(lease).size(), is(1));
    }

    @Test
    public void step3_approveInvoice() throws Exception {
        invoices.createInvoiceNumberNumerator(lease.getFixedAsset(), "OXF-%06d", BigInteger.ZERO);
        List<Invoice> allInvoices = invoices.allInvoices();
        Invoice invoice = allInvoices.get(allInvoices.size() - 1);
        invoice.approve();
        invoice.doInvoice(new LocalDate(2013, 11, 7));
        assertThat(invoice.getInvoiceNumber(), is("OXF-000001"));
        assertThat(invoice.getStatus(), is(InvoiceStatus.INVOICED));
    }

    @Test
    public void step4_indexation() throws Exception {
        Index index = indices.findIndex("ISTAT-FOI");
        indexValues.newIndexValue(index, new LocalDate(2013, 11, 1), new BigDecimal(110));
        indexValues.newIndexValue(index, new LocalDate(2014, 12, 1), new BigDecimal(115));
        lease.verifyUntil(new LocalDate(2015, 3, 31));
        LeaseTermForIndexableRent term = (LeaseTermForIndexableRent) rItem.findTerm(new LocalDate(2015, 1, 1));
        assertThat(term.getIndexationPercentage(), is(new BigDecimal(4.5)));
        assertThat(term.getIndexedValue(), is(new BigDecimal("156750.00")));
        assertThat(totalApporvedOrInvoicedForItem(rItem), is(new BigDecimal("209918.48")));

    }

    @Test
    public void step5_normalInvoice() throws Exception {
        lease.calculate(InvoiceRunType.NORMAL_RUN, InvoiceCalculationSelection.RENT_AND_SERVICE_CHARGE, new LocalDate(2015, 4, 1), new LocalDate(2015, 4, 1), new LocalDate(2015, 4, 1));
        approveInvoices();
        assertThat(totalApporvedOrInvoicedForItem(rItem), is(new BigDecimal("209918.48")));
    }

    @Test
    public void step6_retroInvoice() throws Exception {
        lease.calculate(InvoiceRunType.RETRO_RUN, InvoiceCalculationSelection.RENT_AND_SERVICE_CHARGE, new LocalDate(2015, 4, 1), new LocalDate(2015, 4, 1), new LocalDate(2015, 4, 1));
        // (156750 - 150000) / = 1687.5 added
        approveInvoices();
        assertThat(invoices.findInvoices(lease).size(), is(2));
        assertThat(totalApporvedOrInvoicedForItem(rItem), is(new BigDecimal("209918.48").add(new BigDecimal("1687.50"))));
    }

    @Test
    public void step7_teminate() throws Exception {
        lease.terminate(new LocalDate(2014, 6, 30), true);
        lease.verify();

    }

    // //////////////////////////////////////

    private BigDecimal totalApporvedOrInvoicedForItem(LeaseItem leaseItem) {
        BigDecimal total = BigDecimal.ZERO;
        InvoiceStatus[] allowed = { InvoiceStatus.APPROVED, InvoiceStatus.INVOICED };
        for (InvoiceStatus invoiceStatus : allowed) {
            List<InvoiceItemForLease> items = invoiceItemsForLease.findByLeaseItemAndInvoiceStatus(leaseItem, invoiceStatus);
            for (InvoiceItemForLease item : items) {
                total = total.add(item.getNetAmount());
            }
        }
        return total;
    }

    private void approveInvoices() {
        for (Invoice invoice : invoices.findInvoices(lease)){
            invoice.approve();
        }
    }
}
