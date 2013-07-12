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
package org.estatio.integration.tests;

import java.util.List;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoiceItemsForLease;
import org.estatio.dom.party.Parties;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;
import org.estatio.fixture.invoice.InvoiceAndInvoiceItemFixture;
import org.estatio.integration.EstatioIntegrationTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InvoiceIntegrationTest extends EstatioIntegrationTest {

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution.install(new EstatioTransactionalObjectsFixture());
    }

    @Test
    public void t1_chargeCanBeFound() throws Exception {
        Charge charge = scenarioExecution.service(Charges.class).findChargeByReference("RENT");
        Assert.assertEquals(charge.getReference(), "RENT");
    }

    @Test
    public void t2_invoiceCanBeFound() throws Exception {
        Invoice invoice = scenarioExecution.service(Invoices.class).findMatchingInvoice(scenarioExecution.service(Parties.class).findPartyByReferenceOrName(InvoiceAndInvoiceItemFixture.SELLER_PARTY), scenarioExecution.service(Parties.class).findPartyByReferenceOrName(InvoiceAndInvoiceItemFixture.BUYER_PARTY), PaymentMethod.DIRECT_DEBIT, scenarioExecution.service(Leases.class).findLeaseByReference(InvoiceAndInvoiceItemFixture.LEASE), InvoiceStatus.NEW, InvoiceAndInvoiceItemFixture.START_DATE);
        Assert.assertNotNull(invoice);
    }

    @Test
    public void t3_invoiceItemCanBeFound() throws Exception {
        List<InvoiceItemForLease> invoiceItems = scenarioExecution.service(InvoiceItemsForLease.class).findInvoiceItemsByLease(InvoiceAndInvoiceItemFixture.LEASE, InvoiceAndInvoiceItemFixture.START_DATE, InvoiceAndInvoiceItemFixture.START_DATE);
        Assert.assertThat(invoiceItems.size(), Is.is(2));
    }

    @Test
    public void t4_invoiceCanBeRemoved() throws Exception {
        Invoice invoice = scenarioExecution.service(Invoices.class).findMatchingInvoices(scenarioExecution.service(Parties.class).findPartyByReferenceOrName(InvoiceAndInvoiceItemFixture.SELLER_PARTY), scenarioExecution.service(Parties.class).findPartyByReferenceOrName(InvoiceAndInvoiceItemFixture.BUYER_PARTY), PaymentMethod.DIRECT_DEBIT, scenarioExecution.service(Leases.class).findLeaseByReference(InvoiceAndInvoiceItemFixture.LEASE), InvoiceStatus.NEW, InvoiceAndInvoiceItemFixture.START_DATE).get(0);
        invoice.remove();
        Assert.assertThat(scenarioExecution.service(Invoices.class).findMatchingInvoices(scenarioExecution.service(Parties.class).findPartyByReferenceOrName(InvoiceAndInvoiceItemFixture.SELLER_PARTY), scenarioExecution.service(Parties.class).findPartyByReferenceOrName(InvoiceAndInvoiceItemFixture.BUYER_PARTY), PaymentMethod.DIRECT_DEBIT, scenarioExecution.service(Leases.class).findLeaseByReference(InvoiceAndInvoiceItemFixture.LEASE), InvoiceStatus.NEW, InvoiceAndInvoiceItemFixture.START_DATE).size(), Is.is(0));
    }
    
    @Test
    public void t5_redundant_was_added_for_testing_can_remove_chargeCanBeFound() throws Exception {
        Charge charge = scenarioExecution.service(Charges.class).findChargeByReference("RENT");
        Assert.assertEquals(charge.getReference(), "RENT");
    }

}
