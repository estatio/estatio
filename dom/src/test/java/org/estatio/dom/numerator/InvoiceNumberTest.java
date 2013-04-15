package org.estatio.dom.numerator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Ignoring;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;

import org.estatio.dom.invoice.Invoice;
import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;


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
        invoice.setNumerators(mockNumerators);
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
