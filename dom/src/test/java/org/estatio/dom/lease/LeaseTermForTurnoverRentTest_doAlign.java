package org.estatio.dom.lease;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class LeaseTermForTurnoverRentTest_doAlign {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    private LeaseTermForTurnoverRent term;

    @Mock
    private LeaseItem rentItem;

    @Mock
    private Lease mockLease;

    private LeaseItem torItem;

    private List<LeaseItem> rentItems = new ArrayList<LeaseItem>();

    @Before
    public void setup() {
        context.checking(new Expectations() {
            {
                allowing(mockLease).findItemsOfType(LeaseItemType.RENT);
                will(returnValue(new ArrayList<LeaseItem>() {
                    /**
                     * 
                     */
                    private static final long serialVersionUID = 8666017984548841746L;

                    {
                        add(rentItem);
                    }
                }));
                 oneOf(rentItem).valueForPeriod(with(aNull(InvoicingFrequency.class)),
                 with(any(LocalDate.class)), with(any(LocalDate.class)));
                 will(returnValue(new BigDecimal("100000.00")));
            }
        });

        torItem = new LeaseItem();
        term = new LeaseTermForTurnoverRent();
        term.setLeaseItem(torItem);
        term.setStartDate(new LocalDate(2013, 1, 1));

        torItem.setLease(mockLease);

        rentItem = new LeaseItem();
        rentItem.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        rentItem.setLease(mockLease);

    }

    // @Ignore
    @Test
    public void test() {
        term.setBudgetedTurnoverRent(new BigDecimal("23456.00"));
        term.doAlign();
        assertThat(term.getContractualRent(), is(new BigDecimal("100000.00")));
        assertThat(term.getEffectiveValue(), is(new BigDecimal("23456.00")));
    }

}
