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

    @Mock
    private LeaseItem rentItem;

    @Mock
    private Lease mockLease;

    private LeaseItem torItem;

    @Before
    public void setup() {
        torItem = new LeaseItem();

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
    public void testNothing() {
        tester(null, null, null, "0");
    }

    @Test
    public void testRule() {
        tester("7", null, "1500000.00", "5000.00");
    }

    @Test
    public void testBudget() {
        tester(null, "120000.00", null, "20000.00");
    }

    @Test
    public void testBudgetAndRule() {
        tester("7", "120000.00", "1500000.00", "5000.00");
    }

    
    private void tester(
            final String turnoverRentRule,
            final String totalBudgetedRentStr,
            final String auditedTurnoverStr,
            final String expectedValueStr
            ) {
        LeaseTermForTurnoverRent term = new LeaseTermForTurnoverRent();
        term.setLeaseItem(torItem);
        term.setStartDate(new LocalDate(2013, 1, 1));
        torItem.setLease(mockLease);
        term.setTurnoverRentRule(turnoverRentRule);
        term.setTotalBudgetedRent(parseBigDecimal(totalBudgetedRentStr));
        term.setAuditedTurnover(parseBigDecimal(auditedTurnoverStr));
        term.doAlign();
        assertThat(term.getEffectiveValue(), is(parseBigDecimal(expectedValueStr)));
    }

    private BigDecimal parseBigDecimal(final String input) {
        if (input == null) {
            return null;
        }
        return new BigDecimal(input);
    }
}
