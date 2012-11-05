package com.eurocommercialproperties.estatio.dom.tax;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;

public class TaxTest {

    private LocalDate d1;
    private LocalDate d2;

    private Tax tax;
    private TaxRate r1;
    private TaxRate r2;

    private BigDecimal p1;
    private BigDecimal p2;
    
    @Mock
    Taxes taxes;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {
        context.checking(new Expectations() {
            {
                one(taxes).newTaxRate(with(equal(tax)), with(equal(d1)), with(equal(p1)));
                will(returnValue(r1));
                one(taxes).newTaxRate(with(equal(tax)), with(equal(d2)), with(equal(p2)));
                will(returnValue(r2));
                one(taxes).findTaxRateForDate(with(equal(tax)), with(equal(d1)));
                will(returnValue(r1));
                one(taxes).findTaxRateForDate(with(equal(tax)), with(equal(d2)));
                will(returnValue(r2));
            }
        });
        d1 = new LocalDate(1980, 1, 1);
        d2 = new LocalDate(2000, 1, 1);

        tax = new Tax();
        tax.setTaxRepo(taxes);
        
        r1 = tax.newRate(d1, BigDecimal.valueOf(19));
        r2 = r1.newRate(d2, BigDecimal.valueOf(21));
        
        p1 = BigDecimal.valueOf(19);
        p2 = BigDecimal.valueOf(21);
    }

    @Test
    public void testTaxPercentageForDate() {

        assertEquals(BigDecimal.valueOf(19), tax.getPercentageForDate(d1));
        
    }
}
