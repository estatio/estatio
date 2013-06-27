package org.estatio.dom.invoice;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Ignoring;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.numerator.Numerator;
import org.estatio.dom.numerator.NumeratorType;
import org.estatio.dom.numerator.Numerators;

public class InvoiceTest_assignInvoiceNumber {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    Numerators mockNumerators;
    
    @Ignoring
    @Mock
    DomainObjectContainer mockContainer;

    private Invoice invoice;

    private Numerator inn;

    @Before
    public void setUp() throws Exception {

        inn = new Numerator();
        inn.setLastIncrement(BigInteger.TEN);

        invoice = new Invoice();
        
        invoice.setContainer(mockContainer);
        invoice.injectNumerators(mockNumerators);

        allowingMockNumeratorsRepoToReturn(inn);
    }

    @Test
    public void whenNoInvoiceNumberPreviouslyAssigned() {
        invoice.assignInvoiceNumber();
        assertThat(invoice.getInvoiceNumber(), is("INV-00011"));
    }


    @Test
    public void whenInvoiceNumberAlreadyAssigned() {
        invoice.setInvoiceNumber("SOME-INVOICE-NUMBER");
        invoice.assignInvoiceNumber();
        assertThat(invoice.getInvoiceNumber(), is("SOME-INVOICE-NUMBER"));
    }

    private void allowingMockNumeratorsRepoToReturn(final Numerator inn) {
        context.checking(new Expectations() {
            {
                allowing(mockNumerators).establishNumerator(NumeratorType.INVOICE_NUMBER);
                will(returnValue(inn));
            }
        });
    }
}
