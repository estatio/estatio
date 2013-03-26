package org.estatio.dom.numerator;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.estatio.dom.invoice.Invoice;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InvoiceNumberTest {

    private Invoice invoice;
    private NumeratorForInvoiceNumber in;

    @Before
    public void setup() {
        invoice = new Invoice();
        in = new NumeratorForInvoiceNumber();
    }

    @Test
    public void test() {
        assertThat(in.assign(invoice), is(true));
        Assert.assertEquals("TEST-00001", invoice.getInvoiceNumber());
        assertThat(in.assign(invoice), is(false));
        Assert.assertEquals("TEST-00001", invoice.getInvoiceNumber());
    }

}
