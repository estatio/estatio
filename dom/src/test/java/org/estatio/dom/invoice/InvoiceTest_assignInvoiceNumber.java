package org.estatio.dom.invoice;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import org.estatio.dom.numerator.InvoiceNumberNumerator;
import org.estatio.dom.numerator.NumeratorType;
import org.estatio.dom.numerator.Numerators;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.ClassUnderTest;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Ignoring;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class InvoiceTest_assignInvoiceNumber {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    Numerators mockNumerators;
    
    @Ignoring
    @Mock
    DomainObjectContainer mockContainer;

    @ClassUnderTest
    private Invoice invoice;

    private InvoiceNumberNumerator inn;

    @Before
    public void setUp() throws Exception {

        inn = new InvoiceNumberNumerator();
        inn.setLastIncrement(BigInteger.TEN);

        invoice = context.getClassUnderTest();
        invoice.setContainer(mockContainer);// to fix
        invoice.setNumerators(mockNumerators); // to fix

        allowingMockNumeratorsRepoToReturn(inn);
    }

    @Test
    public void hasNoInvoiceNumber() {
        invoice.assignInvoiceNumber();
        assertThat(invoice.getInvoiceNumber(), is("TEST-00011"));
    }


    @Test
    public void hasInvoiceNumberAlreadyAssigned() {
        invoice.setInvoiceNumber("SOME-INVOICE-NUMBER");
        invoice.assignInvoiceNumber();
        assertThat(invoice.getInvoiceNumber(), is("SOME-INVOICE-NUMBER"));
    }

    private void allowingMockNumeratorsRepoToReturn(final InvoiceNumberNumerator inn) {
        context.checking(new Expectations() {
            {
                allowing(mockNumerators).find(NumeratorType.INVOICE_NUMBER);
                will(returnValue(inn));
            }
        });
    }
}
