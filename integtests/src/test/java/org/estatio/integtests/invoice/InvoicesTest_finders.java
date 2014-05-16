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
package org.estatio.integtests.invoice;

import java.util.List;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioOperationalResetFixture;
import org.estatio.fixture.invoice.InvoiceAndInvoiceItemFixture;
import org.estatio.integtests.EstatioIntegrationTest;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InvoicesTest_finders extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        scenarioExecution().install(new EstatioOperationalResetFixture());
    }


    private Invoices invoices;
    private Parties parties;
    private Leases leases;
    private Party seller;
    private Party buyer;
    private Lease lease;
    private Properties properties;
    private Invoice invoice;
    private static String runId = "2014-02-16T02:30:03.156 - OXF - [OXF-TOPMODEL-001] - [RENT, SERVICE_CHARGE, TURNOVER_RENT, TAX] - 2012-01-01 - 2012-01-01/2012-01-02";

    @Before
    public void setUp() throws Exception {
        invoices = service(Invoices.class);
        parties = service(Parties.class);
        leases = service(Leases.class);
        properties = service(Properties.class);

        seller = parties.findPartyByReference(InvoiceAndInvoiceItemFixture.SELLER_PARTY);
        buyer = parties.findPartyByReference(InvoiceAndInvoiceItemFixture.BUYER_PARTY);
        lease = leases.findLeaseByReference(InvoiceAndInvoiceItemFixture.LEASE);
        invoice = invoices.findOrCreateMatchingInvoice(seller, buyer, PaymentMethod.DIRECT_DEBIT, lease, InvoiceStatus.NEW, InvoiceAndInvoiceItemFixture.START_DATE, null);
        invoice.setRunId(runId);

    }

    @Test
    public void findMatchingInvoice() throws Exception {
        Assert.assertNotNull(invoice);
    }

    @Test
    public void findInvoicesByStatus() {
        List<Invoice> result = invoices.findInvoices(InvoiceStatus.NEW);
        assertThat(result.size(), is(2));
    }

    
    @Test
    public void findInvoicesByPropertyDueDate() {
        Property property = properties.findPropertyByReference("KAL");
        List<Invoice> result = invoices.findInvoices(property, new LocalDate(2012, 1, 1));
        assertThat(result.size(), is(1));
    }

    @Test
    public void findInvoicesByPropertyDueDateStatus() {
        Property property = properties.findPropertyByReference("KAL");
        List<Invoice> result = invoices.findInvoices(property, new LocalDate(2012, 1, 1), InvoiceStatus.NEW);
        assertThat(result.size(), is(1));

    }
    
    @Test
    public void findByRunId() {
        invoices.findInvoices(InvoiceStatus.NEW).get(0).setRunId(runId);
        List<Invoice> result = invoices.findInvoicesByRunId(runId);
        assertThat(result.size(), is(1));
    }

}
