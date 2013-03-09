package org.estatio.dom.index;

import java.math.BigDecimal;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;

import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexationCalculator;
import org.estatio.dom.lease.LeaseTermForIndexableRent;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class IndexCalculatorTest {

    private IndexationCalculator indexCalculator;

    @Mock
    Index mockIndex;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {
        indexCalculator = new IndexationCalculator(mockIndex, new LocalDate(2010, 1, 1), new LocalDate(2011, 1, 1), new BigDecimal(250000));
        indexCalculator.setBaseIndexValue(BigDecimal.valueOf(122.2));
        indexCalculator.setNextIndexValue(BigDecimal.valueOf(111.1));
        indexCalculator.setRebaseFactor(BigDecimal.valueOf(1.234));
    }

    @Test
    public void testIndexationPercentage() {
        context.checking(new Expectations() {
            {
                one(mockIndex).initialize(with(equal(indexCalculator)), with(equal(new LocalDate(2010, 1, 1))), with(equal(new LocalDate(2011, 1, 1))));
            }
        });
        indexCalculator.calculate();
        Assert.assertEquals(BigDecimal.valueOf(12.2), indexCalculator.getIndexationPercentage());
    }

    @Test
    public void testIndexedValue() {
        context.checking(new Expectations() {
            {
                one(mockIndex).initialize(with(equal(indexCalculator)), with(equal(new LocalDate(2010, 1, 1))), with(equal(new LocalDate(2011, 1, 1))));
            }
        });
        indexCalculator.calculate();
        Assert.assertEquals(BigDecimal.valueOf(280500).setScale(4), indexCalculator.getIndexedValue());
    }

}
