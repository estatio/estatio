package org.estatio.integtest.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.numerator.Numerator;
import org.estatio.dom.numerator.NumeratorType;

public class NumeratorIntegrationTest extends AbstractEstatioIntegrationTest {

    @Before
    public void setUp() throws Exception {
        numerators.establish(NumeratorType.INVOICE_NUMBER);
    }

    @Test
    public void t01_numeratorCanBeFound() throws Exception {
        Numerator numerator = numerators.findNumeratorByType(NumeratorType.INVOICE_NUMBER);
        assertNotNull(numerator);
    }

    @Test
    public void t02_canFindUsingNaiveImpl() throws Exception {
        assertThat(numerators.allNumerators().size(), is(1));
    }

    @Test
    public void t03_numberOfNumeratorsIsOne() throws Exception {
        Numerator in = numerators.findNumeratorByType(NumeratorType.INVOICE_NUMBER);
        assertThat(in.getLastIncrement(), is(BigInteger.ZERO));
        assertThat(in.increment(), is(BigInteger.ONE));
        assertThat(in.getLastIncrement(), is(BigInteger.ONE));
    }

}
