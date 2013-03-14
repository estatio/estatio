package org.estatio.dom.invoice;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.ClassUnderTest;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;

import org.estatio.dom.numerator.InvoiceNumberNumerator;
import org.estatio.dom.numerator.NumeratorType;
import org.estatio.dom.numerator.Numerators;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;

public class InvoiceTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    Numerators mockNumerators;
    
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
        allowingMockContainerTitleOf();
    }


    @Test
    public void hasNoInvoiceNumber() {
        context.checking(new Expectations() {
            {
                one(mockContainer).informUser("Assigned TEST-00011 to invoice SOME TITLE");
            }
        });
        // when
        invoice.assignInvoiceNumber();
        // then
        assertThat(invoice.getInvoiceNumber(), is("TEST-00011"));
    }



    @Test
    public void hasInvoiceNumberAlreadyAssigned() {
        // given
        invoice.setInvoiceNumber("SOME-INVOICE-NUMBER");
        // when
        invoice.assignInvoiceNumber();
        // then
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

    private void allowingMockContainerTitleOf() {
        context.checking(new Expectations() {
            {
                allowing(mockContainer).titleOf(with(any(Object.class)));
                will(returnValue("SOME TITLE"));
            }
        });
    }


}
