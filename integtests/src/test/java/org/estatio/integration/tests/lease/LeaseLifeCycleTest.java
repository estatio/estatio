/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForIndexableRent;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.Leases.InvoiceRunType;
import org.estatio.dom.numerator.Numerators;
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
    private LeaseItem leaseRentItem;
    private Invoices invoices;
    private Indices indices;
    private IndexValues indexValues;

    @Before
    public void setup() {
        leases = service(Leases.class);
        indices = service(Indices.class);
        invoices = service(Invoices.class);
        lease = leases.findLeaseByReference("OXF-MIRACL-005");
        leaseRentItem = lease.findFirstItemOfType(LeaseItemType.RENT);
        indexValues = service(IndexValues.class);
    }

    @Test
    public void step1_verify() throws Exception {
        // first pass verify
        lease.verifyUntil(new LocalDate(2015, 1, 1));
        assertThat(leaseRentItem.getTerms().toString(), leaseRentItem.getTerms().size(), is(2));
        // verify is idempotent
        lease.verifyUntil(new LocalDate(2014, 1, 1));
        assertThat(leaseRentItem.getTerms().toString(), leaseRentItem.getTerms().size(), is(2));
        LeaseTermForIndexableRent last = (LeaseTermForIndexableRent) leaseRentItem.getTerms().last();
        LeaseTermForIndexableRent first = (LeaseTermForIndexableRent) leaseRentItem.getTerms().first();
        assertThat(last.getBaseValue(), is(new BigDecimal(150000).setScale(2)));
        assertThat(first.getStartDate(), is(new LocalDate(2013, 11, 7)));
        assertThat(last.getStartDate(), is(new LocalDate(2015, 1, 1)));

    }

    @Test
    public void step2_caluclate() throws Exception {
        assertThat("Before calculation", leaseRentItem.getTerms().size(), is(2));
        lease.calculate(new LocalDate(2013, 11, 13), new LocalDate(2015, 3, 31), START_DATE, InvoiceRunType.NORMAL_RUN);
        assertThat(leaseRentItem.getTerms().size(), is(2));
        LeaseTerm last = leaseRentItem.getTerms().last();
        LeaseTerm first = leaseRentItem.findTerm(START_DATE);
        assertThat(first.getEffectiveInterval().startDate(), is(START_DATE));
        assertThat(totalInvoiced(first), is(new BigDecimal("172418.48")));
        assertThat(totalInvoiced(last), is(new BigDecimal("37500.00")));
    }

    @Test
    public void step3_approveInvoice() throws Exception {
        invoices.createInvoiceNumberNumerator(lease.getProperty(), "OXF-%06d", BigInteger.ZERO);
        List<Invoice> allInvoices = invoices.allInvoices();
        Invoice invoice = allInvoices.get(allInvoices.size() - 1);
        invoice.approve();
        invoice.invoiceOn(new LocalDate(2013, 11, 7));
        assertThat(invoice.getInvoiceNumber(), is("OXF-000001"));
        assertThat(invoice.getStatus(), is(InvoiceStatus.INVOICED));
    }

    @Test
    public void step4_indexation() throws Exception {
        Index index = indices.findIndex("ISTAT-FOI");
        indexValues.newIndexValue(index, new LocalDate(2013, 11, 1), new BigDecimal(110));
        indexValues.newIndexValue(index, new LocalDate(2014, 12, 1), new BigDecimal(115));

        lease.verifyUntil(new LocalDate(2015, 3, 31));

        LeaseTermForIndexableRent term = (LeaseTermForIndexableRent) leaseRentItem.findTerm(new LocalDate(2015, 1, 1));

        assertThat(term.getIndexationPercentage(), is(new BigDecimal(4.5)));

    }

    private BigDecimal totalInvoiced(LeaseTerm term) {
        BigDecimal total = BigDecimal.ZERO;

        for (InvoiceItem item : term.getInvoiceItems()) {
            total = total.add(item.getNetAmount());
        }
        return total;
    }

}
