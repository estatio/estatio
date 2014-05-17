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
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertiesAndUnitsFixture;
import org.estatio.fixture.invoice.InvoiceAndInvoiceItemFixture;
import org.estatio.fixture.lease.LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsAndBreakOptionsFixture;
import org.estatio.fixture.party.PersonsAndOrganisationsAndCommunicationChannelsFixture;
import org.estatio.integtests.EstatioIntegrationTest;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InvoicesTest_findInvoices extends EstatioIntegrationTest {

    private Property kalProperty;

    @Before
    public void setupData() {
        scenarioExecution().install(new CompositeFixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);
                execute("parties", new PersonsAndOrganisationsAndCommunicationChannelsFixture(), executionContext);
                execute("properties", new PropertiesAndUnitsFixture(), executionContext);
                execute("leases", new LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsAndBreakOptionsFixture(), executionContext);
                execute("invoices", new InvoiceAndInvoiceItemFixture(), executionContext);
            }
        });
    }

    private static String runId = "2014-02-16T02:30:03.156 - OXF - [OXF-TOPMODEL-001] - [RENT, SERVICE_CHARGE, TURNOVER_RENT, TAX] - 2012-01-01 - 2012-01-01/2012-01-02";

    private Invoices invoices;
    private Parties parties;
    private Leases leases;
    private Properties properties;

    private Party seller;
    private Party buyer;
    private Lease lease;

    private Invoice invoice;

    @Before
    public void setUp() throws Exception {
        invoices = service(Invoices.class);
        parties = service(Parties.class);
        leases = service(Leases.class);
        properties = service(Properties.class);

        seller = parties.findPartyByReference(InvoiceAndInvoiceItemFixture.SELLER_PARTY);
        buyer = parties.findPartyByReference(InvoiceAndInvoiceItemFixture.BUYER_PARTY);
        lease = leases.findLeaseByReference(InvoiceAndInvoiceItemFixture.LEASE);

        kalProperty = properties.findPropertyByReference("KAL");

        invoice = invoices.findOrCreateMatchingInvoice(seller, buyer, PaymentMethod.DIRECT_DEBIT, lease, InvoiceStatus.NEW, InvoiceAndInvoiceItemFixture.START_DATE, null);
        invoice.setRunId(runId);
        Assert.assertNotNull(invoice);
    }

    @Test
    public void byStatus() {
        List<Invoice> result = invoices.findInvoices(InvoiceStatus.NEW);
        assertThat(result.size(), is(2));
    }

    @Test
    public void byPropertyDueDate() {
        List<Invoice> invoice = invoices.findInvoices(kalProperty, dt(2012, 1, 1));
        assertThat(invoice.size(), is(1));
    }

    @Test
    public void findInvoicesByPropertyDueDateStatus() {

        List<Invoice> invoice = invoices.findInvoices(kalProperty, dt(2012, 1, 1));
        assertThat(invoice.size(), is(1));

        invoice = invoices.findInvoices(kalProperty, dt(2012, 1, 1), InvoiceStatus.NEW);
        assertThat(invoice.size(), is(1));
    }
    
    @Test
    public void findByRunId() {
        final Invoice invoice1 = invoices.findInvoices(InvoiceStatus.NEW).get(0);
        invoice1.setRunId(runId);

        List<Invoice> result = invoices.findInvoicesByRunId(runId);
        assertThat(result.size(), is(1));
    }

}
