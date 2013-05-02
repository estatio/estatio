package org.estatio.integtest.testing;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.PaymentMethod;
import org.estatio.dom.party.Parties;
import org.estatio.integtest.IntegrationSystemForTestRule;
import org.estatio.jdo.ChargesJdo;
import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InvoiceIntegrationTest {

    private Invoices invoices;

    @Rule
    public IntegrationSystemForTestRule webServerRule = new IntegrationSystemForTestRule();

    public IsisSystemForTest getIsft() {
        BasicConfigurator.configure();
        return webServerRule.getIsisSystemForTest();
    }

    @Before
    public void setup() {
        invoices = getIsft().getService(Invoices.class);

    }

    @Test
    public void t1_chargeCanBeFound() throws Exception {
        Charges charges = getIsft().getService(ChargesJdo.class);
        Charge charge = charges.findChargeByReference("RENT");
        Assert.assertEquals(charge.getReference(), "RENT");
    }

    @Test
    public void t2_invoiceCanBeFound() throws Exception {
        Parties parties = getIsft().getService(Parties.class);
        Leases leases = getIsft().getService(Leases.class);
        Invoice invoice = invoices.findMatchingInvoices(parties.findPartyByReference("ACME"), parties.findPartyByReference("TOPMODEL"), PaymentMethod.DIRECT_DEBIT, leases.findByReference("OXF-TOPMODEL-001"), InvoiceStatus.NEW, new LocalDate(2012, 1, 1)).get(0);
        Assert.assertNotNull(invoice);
    }

    @Test
    public void t3_invoiceItemCanBeFound() throws Exception {
        List<InvoiceItem> invoiceItems = invoices.findItems("OXF*", new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1));
        Assert.assertThat(invoiceItems.size(), Is.is(1));
    }

    @Test
    public void t4_invoiceCanBeRemoved() throws Exception {
        Parties parties = getIsft().getService(Parties.class);
        Leases leases = getIsft().getService(Leases.class);
        Invoice invoice = invoices.findMatchingInvoices(parties.findPartyByReference("ACME"), parties.findPartyByReference("TOPMODEL"), PaymentMethod.DIRECT_DEBIT, leases.findByReference("OXF-TOPMODEL-001"), InvoiceStatus.NEW, new LocalDate(2012, 1, 1)).get(0);
        invoice.remove();
    }
}
