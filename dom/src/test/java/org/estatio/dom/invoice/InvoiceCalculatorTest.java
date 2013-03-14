package org.estatio.dom.invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Assert;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.tax.Tax;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class InvoiceCalculatorTest {

    private Lease l;
    private LeaseItem li;
    private LeaseTerm lt;
    private LocalDate startDate = new LocalDate(2011, 11, 1);

    private Tax tax;
    private Charge charge;

    @Mock
    Invoices mockInvoices;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {
        l = new Lease();
        l.setStartDate(startDate);
        l.setEndDate(startDate.plusYears(1).minusDays(1));
        tax = new Tax();
        tax.setReference("VAT");
        charge = new Charge();
        charge.setReference("RENT");
        charge.setTax(tax);
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

        InvoiceCalculator ic = new InvoiceCalculator(lt, new LocalDate(2012, 1, 1));
        ic.calculate();

        Assert.assertEquals(BigDecimal.valueOf(5000).setScale(2, RoundingMode.HALF_UP), ic.getCalculatedValue());
    }

    @Test
    public void testCalculateExactPeriod() {
        li = new LeaseItem();
        li.setStartDate(startDate);
        li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        li.setNextDueDate(new LocalDate(2012, 1, 1));
        l.addToItems(li);

        lt = new LeaseTerm();
        lt.setStartDate(new LocalDate(2012, 1, 1));
        lt.setEndDate(new LocalDate(2012, 3, 31));
        lt.setValue(BigDecimal.valueOf(20000));
        lt.setLeaseItem(li);
        li.addToTerms(lt);

        InvoiceCalculator ic = new InvoiceCalculator(lt, new LocalDate(2012, 1, 1));
        ic.calculate();

        Assert.assertEquals(BigDecimal.valueOf(5000).setScale(2, RoundingMode.HALF_UP), ic.getCalculatedValue());
    }

    @Test
    public void testCalculateSingleMonth() {
        li = new LeaseItem();
        li.setStartDate(startDate);
        li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        li.setNextDueDate(new LocalDate(2012, 1, 1));
        l.addToItems(li);

        lt = new LeaseTerm();
        lt.setStartDate(new LocalDate(2012, 2, 1));
        lt.setEndDate(new LocalDate(2012, 2, 29));
        lt.setValue(BigDecimal.valueOf(20000));
        lt.setLeaseItem(li);
        li.addToTerms(lt);

        InvoiceCalculator ic = new InvoiceCalculator(lt, new LocalDate(2012, 1, 1));
        ic.calculate();

        Assert.assertEquals(BigDecimal.valueOf(1593.41).setScale(2, RoundingMode.HALF_UP), ic.getCalculatedValue());
    }

    @Test
    public void testCalculateNothing() {
        li = new LeaseItem();
        li.setStartDate(startDate);
        li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        li.setNextDueDate(new LocalDate(2012, 1, 1));
        l.addToItems(li);

        lt = new LeaseTerm();
        lt.setStartDate(new LocalDate(2013, 1, 1));
        lt.setEndDate(new LocalDate(2013, 3, 1));
        lt.setValue(BigDecimal.valueOf(20000));
        lt.setLeaseItem(li);
        li.addToTerms(lt);

        InvoiceCalculator ic = new InvoiceCalculator(lt, new LocalDate(2012, 1, 1));
        ic.calculate();

        Assert.assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), ic.getCalculatedValue());
    }

    @Test
    public void testCreateInvoiceItem() {
        li = new LeaseItem();
        li.setStartDate(startDate);
        li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        li.setNextDueDate(new LocalDate(2012, 1, 1));
        li.setCharge(charge);
        l.addToItems(li);
        lt = new LeaseTerm();

        lt.setStartDate(new LocalDate(2012, 1, 1));
        lt.setEndDate(new LocalDate(2013, 1, 1));
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

        InvoiceCalculator ic = new InvoiceCalculator(lt, new LocalDate(2012, 1, 1));
        ic.calculate();
        ic.createInvoiceItems();

        InvoiceItem invoiceItem = lt.getInvoiceItems().iterator().next();

        Assert.assertEquals(BigDecimal.valueOf(5000).setScale(2, RoundingMode.HALF_UP), invoiceItem.getNetAmount());
        Assert.assertEquals(new LocalDate(2012, 1, 1), invoiceItem.getStartDate());
    }
}
