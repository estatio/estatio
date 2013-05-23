package org.estatio.dom.invoice;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;


public class InvoiceTest_compareTo {

    private Invoice item1;
    private Invoice item2;
    private Invoice item3;
    private Invoice item4;
    

    @Before
    public void setup() {
        item1 = new Invoice();
        item2 = new Invoice();
        item3 = new Invoice();
        item4 = new Invoice();
    }

    @Test
    public void invoiceNumber() {
        item1.setInvoiceNumber(null);
        item2.setInvoiceNumber("000002");
        item3.setInvoiceNumber("000002");
        item4.setInvoiceNumber("000003");
        
        assertOrder();
    }

    void assertOrder() {
        assertThat(item1.compareTo(item2), is(Matchers.lessThan(0)));
        assertThat(item2.compareTo(item1), is(Matchers.greaterThan(0)));
        
        assertThat(item2.compareTo(item3), is(0));
        
        assertThat(item3.compareTo(item4), is(Matchers.lessThan(0)));
        assertThat(item4.compareTo(item3), is(Matchers.greaterThan(0)));
    }
}
