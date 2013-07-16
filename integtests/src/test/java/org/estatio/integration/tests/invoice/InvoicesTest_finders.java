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
package org.estatio.integration.tests.invoice;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;
import org.estatio.fixture.invoice.InvoiceAndInvoiceItemFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;

public class InvoicesTest_finders extends EstatioIntegrationTest {

    private Invoices invoices;
    private Parties parties;
    private Leases leases;
    private Party seller;
    private Party buyer;
    private Lease lease;

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioTransactionalObjectsFixture());
    }

    @Before
    public void setUp() throws Exception {
        invoices = service(Invoices.class);
        parties = service(Parties.class);
        leases = service(Leases.class);
        
        seller = parties.findPartyByReferenceOrName(InvoiceAndInvoiceItemFixture.SELLER_PARTY);
        buyer = parties.findPartyByReferenceOrName(InvoiceAndInvoiceItemFixture.BUYER_PARTY);
        lease = leases.findLeaseByReference(InvoiceAndInvoiceItemFixture.LEASE);
    }
    
    @Test
    public void findMatchingInvoice() throws Exception {
        Invoice invoice = invoices.findMatchingInvoice(seller, buyer, PaymentMethod.DIRECT_DEBIT, lease, InvoiceStatus.NEW, InvoiceAndInvoiceItemFixture.START_DATE);
        Assert.assertNotNull(invoice);
    }

}
