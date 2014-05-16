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
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InvoiceTest_remove extends EstatioIntegrationTest {

    private Invoices invoices;
    private Parties parties;
    private Leases leases;
    
    private Party seller;
    private Party buyer;
    private Lease lease;

    @Before
    public void setupData() {
        scenarioExecution().install(new EstatioOperationalResetFixture());
    }

    @Before
    public void setUp() throws Exception {
        invoices = service(Invoices.class);
        parties = service(Parties.class);
        leases = service(Leases.class);
        
        seller = parties.findPartyByReference(InvoiceAndInvoiceItemFixture.SELLER_PARTY);
        buyer = parties.findPartyByReference(InvoiceAndInvoiceItemFixture.BUYER_PARTY);
        lease = leases.findLeaseByReference(InvoiceAndInvoiceItemFixture.LEASE);
    }
    
    @Test
    public void happyCase() throws Exception {
        // given
        Assert.assertThat(findMatchingInvoices(seller, buyer, lease).size(), Is.is(1));
        Invoice invoice = findMatchingInvoices(seller, buyer, lease).get(0);
        // when
        invoice.remove();
        // then
        Assert.assertThat(findMatchingInvoices(seller, buyer, lease).size(), Is.is(0));
    }

    private List<Invoice> findMatchingInvoices(final Party seller, final Party buyer, final Lease lease) {
        return invoices.findMatchingInvoices(seller, buyer, PaymentMethod.DIRECT_DEBIT, lease, InvoiceStatus.NEW, InvoiceAndInvoiceItemFixture.START_DATE);
    }

}
