package org.estatio.dom.lease;

import java.math.BigDecimal;

import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexBase;
import org.estatio.dom.index.IndexValue;
import org.hamcrest.core.Is;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;

public class LeaseTermForServiceChargeTest {

    private LeaseItem item;
    private LeaseTermForServiceCharge term;

    public Index i;

    private IndexBase ib1;
    private IndexBase ib2;
    private IndexValue iv1;
    private IndexValue iv2;

    @Mock
    LeaseTerms mockLeaseTerms;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {
        item = new LeaseItem();
        item.setType(LeaseItemType.SERVICE_CHARGE);
        item.setLeaseTermsService(mockLeaseTerms);

        term = new LeaseTermForServiceCharge();
        term.modifyLeaseItem(item);
        term.initialize();
        term.setStartDate(new LocalDate(2011, 1, 1));
        term.setBudgetedValue(BigDecimal.valueOf(6000).setScale(4));
        
    }

    @Test
    public void testUpdate() {
//        context.checking(new Expectations() {
//            {
//                allowing(mockLeaseTerms).newLeaseTerm(with(any(LeaseItem.class)), with(any(LeaseTerm.class)));
//                will(returnValue(new LeaseTermForServiceCharge()));
//            }
//        });
//        term.createNext();
        term.update();
        Assert.assertThat(term.getValue(), Is.is(term.getBudgetedValue()));
        LeaseTermForServiceCharge nextTerm = new LeaseTermForServiceCharge();
        nextTerm.setPreviousTerm(term);
        nextTerm.initialize();
        nextTerm.update();
        Assert.assertThat(nextTerm.getBudgetedValue(), Is.is(term.getBudgetedValue()));
        Assert.assertThat(nextTerm.getValue(), Is.is(term.getValue()));
    }
    
    @Test
    public void testValueForDueDate() throws Exception {
        LeaseTermForServiceCharge term = new LeaseTermForServiceCharge();
        term.setEndDate(new LocalDate(2011, 12, 31));
        term.setBudgetedValue(BigDecimal.valueOf(6000));
        term.setAuditedValue(BigDecimal.valueOf(6600));
        Assert.assertThat(term.valueForDueDate(new LocalDate(2011, 1, 1)), Is.is(BigDecimal.valueOf(6000)));
        Assert.assertThat(term.valueForDueDate(new LocalDate(2011, 12, 31)), Is.is(BigDecimal.valueOf(6000)));
        Assert.assertThat(term.valueForDueDate(new LocalDate(2012, 1, 1)), Is.is(BigDecimal.valueOf(6600)));
        Assert.assertThat(term.valueForDueDate(new LocalDate(2012, 7, 31)), Is.is(BigDecimal.valueOf(6600)));
    }

}
