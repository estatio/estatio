package org.estatio.dom.invoice;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.tax.Tax;

public class InvoiceItemTest_tax {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    Tax mockTax;

    private InvoiceItem invoiceItem;
    private LocalDate date = new LocalDate(2014,1,1);
    

    @Before
    public void setup() {
        context.checking(new Expectations() {
            {
                allowing(mockTax).percentageFor(with(anyOf(aNull(LocalDate.class),any(LocalDate.class))));
                will(returnValue(new BigDecimal("17.5")));
            }
        });
        invoiceItem = new InvoiceItem();
        invoiceItem.setTax(mockTax);

    }

    @Test
    public void test() {
        invoiceItem.setNetAmount(new BigDecimal("100.00"));
        invoiceItem.verify();
        assertThat(invoiceItem.getVatAmount(), is(new BigDecimal("17.50")));
        assertThat(invoiceItem.getGrossAmount(), is(new BigDecimal("117.50")));
    }

    @Test
    public void test2() {
        invoiceItem.setNetAmount(new BigDecimal("1.50"));
        invoiceItem.verify();
        assertThat(invoiceItem.getVatAmount(), is(new BigDecimal("0.26")));
        assertThat(invoiceItem.getGrossAmount(), is(new BigDecimal("1.76")));
    }

    @Test
    public void test3() {
        invoiceItem.setTax(null);
        invoiceItem.setNetAmount(new BigDecimal("1.50"));
        invoiceItem.verify();
        assertThat(invoiceItem.getVatAmount(), is(new BigDecimal("0")));
        assertThat(invoiceItem.getGrossAmount(), is(new BigDecimal("1.50")));
    }

    
}
