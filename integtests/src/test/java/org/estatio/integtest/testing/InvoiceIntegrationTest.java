package org.estatio.integtest.testing;

import java.util.List;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.fixture.invoice.InvoiceFixture;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InvoiceIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void t1_chargeCanBeFound() throws Exception {
        Charge charge = charges.findChargeByReference("RENT");
        Assert.assertEquals(charge.getReference(), "RENT");
    }

    @Test
    public void t2_invoiceCanBeFound() throws Exception {
        Invoice invoice = invoices.findMatchingInvoices(parties.findPartyByReference(InvoiceFixture.SELLER_PARTY), parties.findPartyByReference(InvoiceFixture.BUYER_PARTY), PaymentMethod.DIRECT_DEBIT, leases.findByReference(InvoiceFixture.LEASE), InvoiceStatus.NEW, InvoiceFixture.DATE).get(0);
        Assert.assertNotNull(invoice);
    }

    @Test
    public void t3_invoiceItemCanBeFound() throws Exception {
        List<InvoiceItemForLease> invoiceItems = invoices.findItems("OXF-MEDIA*", InvoiceFixture.DATE, InvoiceFixture.DATE);
        Assert.assertThat(invoiceItems.size(), Is.is(1));
    }

    @Test
    public void t4_invoiceCanBeRemoved() throws Exception {
        Invoice invoice = invoices.findMatchingInvoices(parties.findPartyByReference(InvoiceFixture.SELLER_PARTY), parties.findPartyByReference(InvoiceFixture.BUYER_PARTY), PaymentMethod.DIRECT_DEBIT, leases.findByReference(InvoiceFixture.LEASE), InvoiceStatus.NEW, InvoiceFixture.DATE).get(0);
        invoice.remove();
        Assert.assertThat(invoices.findMatchingInvoices(parties.findPartyByReference(InvoiceFixture.SELLER_PARTY), parties.findPartyByReference(InvoiceFixture.BUYER_PARTY), PaymentMethod.DIRECT_DEBIT, leases.findByReference(InvoiceFixture.LEASE), InvoiceStatus.NEW, InvoiceFixture.DATE).size(), Is.is(0));
    }
}
