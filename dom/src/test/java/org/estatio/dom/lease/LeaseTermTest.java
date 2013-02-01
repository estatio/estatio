package org.estatio.dom.lease;

import static org.junit.Assert.fail;

import java.math.BigDecimal;

import junit.framework.Assert;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;

import org.estatio.dom.index.Index;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LeaseTermTest {

    private Lease l;
    private LeaseItem li;
    private LeaseTerm lt;
    private LocalDate startDate = new LocalDate(2011,11,1);

    @Mock
    LeaseItem mockLeaseItem;

    @Mock
    Index mockIndex;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {

        l = new Lease();
        l.setStartDate(startDate);
        l.setEndDate(startDate.plusYears(1).minusDays(1));

        li = new LeaseItem();
        l.addToItems(li);
        li.setStartDate(startDate);
        li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        li.setNextDueDate(new LocalDate(2012, 1, 1));

        lt = new LeaseTerm();
        li.addToTerms(lt);
        lt.setStartDate(startDate);
        lt.setValue(BigDecimal.valueOf(20000));

    }

    @Test
    public void testCalculation1() {
       Assert.assertEquals(lt.calculate(new LocalDate(2012,1,1), new LocalDate(2012,3,31)), BigDecimal.valueOf(5000));

    }

}
