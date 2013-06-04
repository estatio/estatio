package org.estatio.dom.invoice;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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


public class InvoiceNumberTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    private Invoice invoice;
    private Numerator numerator;

    @Mock
    public Numerators mockNumerators;
    @Mock
    @Ignoring
    private DomainObjectContainer mockContainer;

    
    @Before
    public void setup() {
        invoice = new Invoice();
        numerator = new Numerator();
        invoice.injectNumerators(mockNumerators);
        invoice.setContainer(mockContainer);
    }

    @Test
    public void test() {
        context.checking(new Expectations() {
            {
                allowing(mockNumerators).establish(NumeratorType.INVOICE_NUMBER);
                will(returnValue(numerator));
            }
        });
        
        invoice.assignInvoiceNumber();
        assertThat(invoice.getInvoiceNumber(), is("INV-00001"));
    }

}
