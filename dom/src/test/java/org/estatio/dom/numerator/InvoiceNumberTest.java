package org.estatio.dom.numerator;

import static org.junit.Assert.fail;

import org.estatio.dom.invoice.Invoice;
import org.junit.Test;

public class InvoiceNumberTest {

    @Test
    public void test() {
        Invoice invoice = new Invoice();
        invoice.assignInvoiceNumber();
    }

}
