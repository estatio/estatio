package org.estatio.integtest.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.PaymentMethod;
import org.estatio.dom.party.Parties;
import org.estatio.integtest.IntegrationSystemForTestRule;
import org.estatio.jdo.ChargesJdo;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;

public class InvoiceIntegrationTest {

    @Rule
    public IntegrationSystemForTestRule webServerRule = new IntegrationSystemForTestRule();

    public IsisSystemForTest getIsft() {
        return webServerRule.getIsisSystemForTest();
    }

    @Test
    public void numberOfChargesIsOne() throws Exception {
        Charges charges = getIsft().getService(ChargesJdo.class);
        assertThat(charges.allCharges().size(), is(2));
    }

    @Test
    public void chargeCanBeFound() throws Exception {
        Charges charges = getIsft().getService(ChargesJdo.class);
        Charge charge = charges.findChargeByReference("RENT");
        Assert.assertEquals(charge.getReference(), "RENT");
    }

    @Test
    public void invoiceCanBeFound() throws Exception {
        Parties parties = getIsft().getService(Parties.class);
        Leases leases = getIsft().getService(Leases.class);
        Invoices invoices = getIsft().getService(Invoices.class);
        Invoice invoice = invoices.findMatchingInvoices(parties.findPartyByReference("ACME"), parties.findPartyByReference("TOPMODEL"), PaymentMethod.DIRECT_DEBIT, leases.findByReference("OXF-TOPMODEL-001"), InvoiceStatus.NEW).get(0);
        Assert.assertNotNull(invoice);
    }
}
