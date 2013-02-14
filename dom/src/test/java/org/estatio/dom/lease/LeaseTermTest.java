package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.RoundingMode;

import junit.framework.Assert;

import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.invoice.Invoices;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;

public class LeaseTermTest {

    private Lease l;
    private LeaseItem li;
    private LeaseTerm lt;
    private LocalDate startDate = new LocalDate(2011, 11, 1);
    
    @Mock
    Invoices mockInvoices;
    
    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
    
    
    
    @Before
    public void setup() {
        l = new Lease();
        l.setStartDate(startDate);
        l.setEndDate(startDate.plusYears(1).minusDays(1));
    }

    @Test
    public void testCalculateFullQuarter() {
        li = new LeaseItem();
        li.setStartDate(startDate);
        li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        li.setNextDueDate(new LocalDate(2012, 1, 1));
        l.addToItems(li);

        lt = new LeaseTerm();
        lt.setStartDate(startDate);
        lt.setValue(BigDecimal.valueOf(20000));
        lt.setLeaseItem(li);
        li.addToTerms(lt);

        Assert.assertEquals(BigDecimal.valueOf(5000).setScale(2, RoundingMode.HALF_UP), lt.calculate(new LocalDate(2012, 1, 1)));
    }

    @Test
    public void testCalculateExactPeriod() {
        li = new LeaseItem();
        li.setStartDate(startDate);
        li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        li.setNextDueDate(new LocalDate(2012, 1, 1));
        l.addToItems(li);

        lt = new LeaseTerm();
        lt.setStartDate(new LocalDate(2012,1,1));
        lt.setEndDate(new LocalDate(2012,3,31));
        lt.setValue(BigDecimal.valueOf(20000));
        lt.setLeaseItem(li);
        li.addToTerms(lt);

        Assert.assertEquals(BigDecimal.valueOf(5000).setScale(2, RoundingMode.HALF_UP), lt.calculate(new LocalDate(2012, 1, 1)));
    }
    
    @Test
    public void testCalculateSingleMonth() {
        li = new LeaseItem();
        li.setStartDate(startDate);
        li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        li.setNextDueDate(new LocalDate(2012, 1, 1));
        l.addToItems(li);

        lt = new LeaseTerm();
        lt.setStartDate(new LocalDate(2012,2,1));
        lt.setEndDate(new LocalDate(2012,2,29));
        lt.setValue(BigDecimal.valueOf(20000));
        lt.setLeaseItem(li);
        li.addToTerms(lt);
        Assert.assertEquals(BigDecimal.valueOf(1593.41).setScale(2, RoundingMode.HALF_UP), lt.calculate(new LocalDate(2012, 1, 1)));
    }

    @Test
    public void testCalculateNothing() {
        li = new LeaseItem();
        li.setStartDate(startDate);
        li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        li.setNextDueDate(new LocalDate(2012, 1, 1));
        l.addToItems(li);

        lt = new LeaseTerm();
        lt.setStartDate(new LocalDate(2013,1,1));
        lt.setEndDate(new LocalDate(2013,3,1));
        lt.setValue(BigDecimal.valueOf(20000));
        lt.setLeaseItem(li);
        li.addToTerms(lt);

        Assert.assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), lt.calculate(new LocalDate(2012, 1, 1)));
    }
    
    @Test
    public void testCreateInvoiceItem() {
        li = new LeaseItem();
        li.setStartDate(startDate);
        li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        li.setNextDueDate(new LocalDate(2012, 1, 1));
        l.addToItems(li);

        lt = new LeaseTerm();
        lt.setStartDate(new LocalDate(2013,1,1));
        lt.setEndDate(new LocalDate(2013,3,1));
        lt.setValue(BigDecimal.valueOf(20000));
        lt.setLeaseItem(li);
        li.addToTerms(lt);
        
        lt.setInvoiceService(mockInvoices);
        
        context.checking(new Expectations() {
            {
                allowing(mockInvoices).newInvoiceItem();
                will(returnValue(new InvoiceItem()));
            }
        });
        
        lt.createInvoiceItems(new LocalDate(2012, 1, 1));
        
        Assert.assertEquals(1, lt.getInvoiceItems().size());
    }
}
