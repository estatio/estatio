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
import javax.inject.Inject;
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
import org.estatio.fixture.asset.PropertyForKal;
import org.estatio.fixture.invoice.InvoiceForKalPoison001;
import org.estatio.fixture.invoice.InvoiceForOxfPoison003;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InvoicesTest_findInvoices extends EstatioIntegrationTest {

    private Property propertyKal;

    @Before
    public void setupData() {
        scenarioExecution().install(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);

                execute(new InvoiceForOxfPoison003(), executionContext);
                execute(new InvoiceForKalPoison001(), executionContext);
            }
        });
    }

    private static String runId = "2014-02-16T02:30:03.156 - OXF - [OXF-TOPMODEL-001] - [RENT, SERVICE_CHARGE, TURNOVER_RENT, TAX] - 2012-01-01 - 2012-01-01/2012-01-02";

    @Inject
    private Invoices invoices;
    @Inject
    private Parties parties;
    @Inject
    private Leases leases;
    @Inject
    private Properties properties;

    @Before
    public void setUp() throws Exception {
        Party seller = parties.findPartyByReference(InvoiceForKalPoison001.SELLER_PARTY);
        Party buyer = parties.findPartyByReference(InvoiceForKalPoison001.BUYER_PARTY);
        Lease lease = leases.findLeaseByReference(InvoiceForKalPoison001.LEASE);

        propertyKal = properties.findPropertyByReference(PropertyForKal.PROPERTY_REFERENCE);

        Invoice invoice = invoices.findOrCreateMatchingInvoice(seller, buyer, PaymentMethod.DIRECT_DEBIT, lease, InvoiceStatus.NEW, InvoiceForKalPoison001.START_DATE, null);
        invoice.setRunId(runId);
        Assert.assertNotNull(invoice);
    }

    @Test
    public void byStatus() {
        List<Invoice> invoiceList = invoices.findInvoices(InvoiceStatus.NEW);
        assertThat(invoiceList.size(), is(2));
    }

    @Test
    public void byPropertyDueDate() {
        List<Invoice> invoiceList = invoices.findInvoices(propertyKal, VT.ld(2012, 1, 1));
        assertThat(invoiceList.size(), is(1));
    }

    @Test
    public void byPropertyDueDateStatus() {
        List<Invoice> invoiceList = invoices.findInvoices(propertyKal, VT.ld(2012, 1, 1), InvoiceStatus.NEW);
        assertThat(invoiceList.size(), is(1));
    }

}
