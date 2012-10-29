package com.eurocommercialproperties.estatio.dom.tax;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;
import com.eurocommercialproperties.estatio.dom.index.Index;
import com.eurocommercialproperties.estatio.dom.index.IndexBase;
import com.eurocommercialproperties.estatio.dom.index.IndexValue;
import com.eurocommercialproperties.estatio.dom.index.Indices;

public class TaxTest {


    @Mock
    Taxes taxes;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {
        
        LocalDate d1 = new LocalDate(1980,1,1);
        LocalDate d2 = new LocalDate(2000,1,1);
        
        Tax tax = new Tax();
        TaxRate r1 = tax.newRate(d1, BigDecimal.valueOf(19));
        TaxRate r2 = r1.newRate(d2, BigDecimal.valueOf(21));
 
        
        //TODO: Finalize
     }

    @Test
    public void testTaxPercentageForDate() {

    
    }
}
