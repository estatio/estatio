package org.estatio.dom.numerator;

import org.estatio.dom.invoice.Invoice;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InvoiceNumberTest {

    private Invoice invoice;
    private InvoiceNumber in;

    
    @Before
    public void setup() {
        invoice = new Invoice();
        in = new InvoiceNumber();
    }
    
    @Test
    public void test() {
        in.assign(invoice);
        Assert.assertEquals("TEST-00001", invoice.getInvoiceNumber());
        in.assign(invoice);
        Assert.assertEquals("TEST-00001", invoice.getInvoiceNumber());

    }

}
