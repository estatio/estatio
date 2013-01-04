package com.eurocommercialproperties.estatio.dom.lease;

import java.math.BigDecimal;

import junit.framework.Assert;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;
import com.eurocommercialproperties.estatio.dom.index.Index;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class IndexableLeaseTermTest {

    private LeaseTermForIndexableRent ilt ;
    private LeaseItem li;
    private Index i;
    
    @Mock
    LeaseItem mockLeaseItem;
    
    @Mock
    Index mockIndex;

    
    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {
        li= new LeaseItem();
        
        ilt = new LeaseTermForIndexableRent();
        ilt.setBaseIndexValue(BigDecimal.valueOf(122.2));
        ilt.setNextIndexValue(BigDecimal.valueOf(111.1));
        ilt.setBaseValue(BigDecimal.valueOf(250000));
        ilt.setLeaseItem(li);
        
    }

    @Ignore
    @Test
    public void verifyRunsWell() {
        context.checking(new Expectations() {
            {
                one(mockLeaseItem).getIndex();
                will(returnValue(i));
            }
        });

        ilt.verify();
        Assert.assertEquals(BigDecimal.valueOf(280000), ilt.getIndexedValue());
    }
    
}
