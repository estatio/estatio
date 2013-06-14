package org.estatio.dom.lease;

import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.hamcrest.Description;
import org.hamcrest.core.Is;
import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.services.clock.ClockService;

public class LeaseTermTest {

    private Lease lease;
    private LeaseTermForTesting term;
    private LeaseItem item;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    LeaseTerms mockLeaseTerms;

    @Mock
    private static ClockService mockClockService;

    private final LocalDate now = LocalDate.now();

    @Before
    public void setUp() throws Exception {

        context.checking(new Expectations() {
            {
                allowing(mockClockService).now();
                will(returnValue(now));
            }
        });

        lease = new Lease();
        item = new LeaseItem();
        item.modifyLease(lease);
        item.setEndDate(new LocalDate(2013, 6, 30));
        item.injectLeaseTerms(mockLeaseTerms);
        item.injectClockService(mockClockService);

        term = new LeaseTermForTesting();
        term.modifyLeaseItem(item);
        term.setStartDate(new LocalDate(2012, 1, 1));
        // term.setEndDate(new LocalDate(2999, 9, 9));
        term.setFrequency(LeaseTermFrequency.YEARLY);
        term.injectClockService(mockClockService);
        term.initialize();
    }

    @Test
    public void createNext_ok() {
        final LeaseTermForTesting mockTerm = new LeaseTermForTesting();
        mockTerm.modifyLeaseItem(item);
        context.checking(new Expectations() {
            {
                oneOf(mockLeaseTerms).newLeaseTerm(with(any(LeaseItem.class)), with(any(LeaseTerm.class)));
                will(returnLeaseTerm());
            }
        });
        LeaseTermForTesting next = (LeaseTermForTesting) term.createNext(new LocalDate(2013, 1, 1));
        Assert.assertThat(term.getEndDate(), Is.is(new LocalDate(2012, 12, 31)));
        Assert.assertThat(next.getStartDate(), Is.is(new LocalDate(2013, 1, 1)));
        Assert.assertNull(next.getEndDate());
    }

    @Test
    public void update_ok() {
        LeaseTermForTesting nextTerm = new LeaseTermForTesting();
        nextTerm.modifyPreviousTerm(term);
        nextTerm.modifyStartDate(new LocalDate(2013, 1, 1));
        // term.update();
        assertThat(term.getEndDate(), Is.is(new LocalDate(2012, 12, 31)));
    }

    @Test
    public void verify_ok() {
        context.checking(new Expectations() {
            {
                oneOf(mockLeaseTerms).newLeaseTerm(with(any(LeaseItem.class)), with(any(LeaseTerm.class)));
                will(returnLeaseTerm());
            }
        });
        LeaseTerm newTerm = new LeaseTermForTesting();
        newTerm.injectClockService(mockClockService);
        newTerm.modifyLeaseItem(item);
        newTerm.setStartDate(new LocalDate(2012, 1, 1));
        newTerm.setFrequency(LeaseTermFrequency.YEARLY);
        newTerm.initialize();
        newTerm.verify();
        assertThat(newTerm.getEndDate(), Is.is(new LocalDate(2012, 12, 31)));
    }

    @Test
    public void invoicedValueFor_ok() throws Exception {
        LocalDate date = new LocalDate(2012, 1, 1);
        LeaseTermForTesting term = new LeaseTermForTesting();
        Invoice invoice = new Invoice();
        invoice.setStatus(InvoiceStatus.APPROVED);
        InvoiceItemForLease item1 = new InvoiceItemForLease();
        item1.modifyInvoice(invoice);
        item1.modifyLeaseTerm(term);
        item1.setStartDate(date);
        item1.setNetAmount(BigDecimal.valueOf(1234.45));
        InvoiceItemForLease item2 = new InvoiceItemForLease();
        item2.modifyInvoice(invoice);
        item2.setNetAmount(BigDecimal.valueOf(1234.45));
        item2.modifyLeaseTerm(term);
        item2.setStartDate(date);

        Assert.assertThat(term.invoicedValueFor(date), Is.is(new BigDecimal("2468.90")));
    }

    // //////////////////////////////////////

    public static Action returnLeaseTerm() {
        return new Action() {
            @Override
            public Object invoke(Invocation invocation) throws Throwable {
                LeaseItem leaseItem = (LeaseItem) invocation.getParameter(0);
                LeaseTerm leaseTerm = (LeaseTerm) invocation.getParameter(1);
                LeaseTermForTesting ltt = new LeaseTermForTesting();
                ltt.modifyLeaseItem(leaseItem);
                ltt.modifyPreviousTerm(leaseTerm);
                ltt.initialize();
                ltt.injectClockService(mockClockService);
                return ltt;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("new Lease Term under item and with previous term");
            }
        };
    }
}
