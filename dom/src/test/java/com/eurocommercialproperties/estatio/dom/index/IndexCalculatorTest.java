package com.eurocommercialproperties.estatio.dom.index;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;

public class IndexCalculatorTest {

    private IndexationCalculator c;
    private BigDecimal[] values;

    @Mock
    Index mockIndex;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {
        c = new IndexationCalculator(mockIndex, new LocalDate(2010, 1, 1), new LocalDate(2010, 1, 31), new LocalDate(2011, 1, 1), new LocalDate(2011, 1, 31), new BigDecimal(100000));
        values = new BigDecimal[] { BigDecimal.valueOf(122.2), BigDecimal.valueOf(111.1), BigDecimal.valueOf(1.234) };
    }

    @Test
    public void test() {
        context.checking(new Expectations() {
            {
                one(mockIndex).getIndexationValues(with(equal(new LocalDate(2010, 1, 1))), with(equal(new LocalDate(2011, 1, 1))));
                will(returnValue(values));
            }
        });
        c.calculate();
        Assert.assertEquals(BigDecimal.valueOf(12), c.getIndexationPercentage());
    }
}
