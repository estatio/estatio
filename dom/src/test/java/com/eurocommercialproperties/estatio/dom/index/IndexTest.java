package com.eurocommercialproperties.estatio.dom.index;


import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;


public class IndexTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_ONLY);


    @Mock
    private Indices mockIndices;
    
    @Test
    public void test() {
        
        //122.2 / 110.1 * 1.345 * 1.234
        
        Index index = new Index();
        index.setIndices(mockIndices);
        IndexBase ib1 = new IndexBase();
        ib1.setFactor(BigDecimal.valueOf(1.345));
        
        IndexBase ib2 = new IndexBase();
        ib2.setPreviousBase(ib1);
        ib2.setFactor(BigDecimal.valueOf(1.234));
               
        IndexValue iv1 = new IndexValue();
        iv1.setIndexBase(ib1);
        iv1.setStartDate(new LocalDate(2001,1,1));
        
        IndexValue iv2 = new IndexValue();
        iv2.setIndexBase(ib2);
        iv2.setStartDate(new LocalDate(2011,1,1));

        
        
        LocalDate nextDate;
        LocalDate baseDate;
        //index.getIndexValueForDate(startDate, endDate)
//        index.getIndexationFactor(baseDate, nextDate);
       
        
        context.checking(new Expectations() {
            {
                allowing(mockIndices).findIndexValueForDate(
                        with(equal(new LocalDate(2011,1,1))),
                        with(equal(new LocalDate(2011,1,31)))
                        );
                will(returnValue(new IndexValue()));
            }
        });
        
        fail("Not yet implemented");
    }

}
