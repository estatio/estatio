package org.estatio.dom.lease;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.invoice.InvoicingInterval;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService.CalculationResult;
import org.estatio.dom.valuetypes.LocalDateInterval;

public class LeaseTermForTurnoverRentTest_doAlign {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    private LeaseTermForTurnoverRent term;

    @Mock
    private LeaseItem rentItem;

    @Mock
    private Lease mockLease;

    private LeaseItem torItem;

    @Before
    public void setup() {
        torItem = new LeaseItem();
        term = new LeaseTermForTurnoverRent();
        term.setLeaseItem(torItem);
        term.setStartDate(new LocalDate(2013, 1, 1));
        torItem.setLease(mockLease);

        context.checking(new Expectations() {
            {
                oneOf(mockLease).findItemsOfType(LeaseItemType.RENT);
                will(returnValue(new ArrayList<LeaseItem>() {
                    private static final long serialVersionUID = 8666017984548841746L;
                    {
                        add(rentItem);
                    }
                }));
                oneOf(rentItem).getInvoicingFrequency();
                will(returnValue(InvoicingFrequency.QUARTERLY_IN_ADVANCE));
                oneOf(rentItem).calculationResults(with(any(InvoicingFrequency.class)), with(any(LocalDate.class)), with(any(LocalDate.class)));
                will(returnValue(new ArrayList<CalculationResult>() {
                    private static final long serialVersionUID = -2212720554866561882L;
                    {
                        add(new CalculationResult(
                                new InvoicingInterval(LocalDateInterval.parseString("2013-01-01/2013-04-01"), new LocalDate(2013, 1, 1)),
                                LocalDateInterval.parseString("2013-01-01/2013-04-01"),
                                new BigDecimal("100000.00"),
                                BigDecimal.ZERO,
                                BigDecimal.ZERO
                        ));
                    }
                }));
            }
        });

    }

    @Test
    public void test() {
        term.setBudgetedTurnoverRent(new BigDecimal("23456.00"));
        term.doAlign();
        assertThat(term.getContractualRent(), is(new BigDecimal("100000.00")));
        assertThat(term.getEffectiveValue(), is(new BigDecimal("23456.00")));
    }

}
