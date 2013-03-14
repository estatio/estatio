package org.estatio.integtest.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.estatio.dom.numerator.InvoiceNumberNumerator;
import org.estatio.dom.numerator.NumeratorType;
import org.estatio.dom.numerator.Numerators;
import org.estatio.integtest.IntegrationSystemForTestRule;
import org.estatio.jdo.NumeratorsJdo;
import org.junit.Rule;
import org.junit.Test;

public class NumeratorIntegrationTest {

    @Rule
    public IntegrationSystemForTestRule webServerRule = new IntegrationSystemForTestRule();

    public IsisSystemForTest getIsft() {
        return webServerRule.getIsisSystemForTest();
    }

    @Test
    public void numeratorCanBeFound() throws Exception {
        Numerators numerators = getIsft().getService(NumeratorsJdo.class);
        numerators.create(NumeratorType.INVOICE_NUMBER);
        assertNotNull(numerators.find(NumeratorType.INVOICE_NUMBER));
    }
    
    @Test
    public void numberOfNumeratorsIsOne() throws Exception {
        Numerators numerators = getIsft().getService(NumeratorsJdo.class);
        InvoiceNumberNumerator in = (InvoiceNumberNumerator) numerators.findOrCreate(NumeratorType.INVOICE_NUMBER);
        assertThat(in.getLastIncrement(), is(BigInteger.ZERO));
        assertThat(in.increment(), is(BigInteger.ONE));
        assertNotNull(numerators.find(NumeratorType.INVOICE_NUMBER));
        assertThat(numerators.allNumerators().size(), is(1));
    }

//    @Test
//    public void proveThatNaiveImplGoesWrong() throws Exception {
//        Numerators numerators = getIsft().getService(Numerators.class);
//        numerators.create(NumeratorType.INVOICE_NUMBER);
//        assertNotNull(numerators.find(NumeratorType.INVOICE_NUMBER));
//    }

    
    
}
