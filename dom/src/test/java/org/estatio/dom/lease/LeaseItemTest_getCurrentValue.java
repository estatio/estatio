package org.estatio.dom.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.services.clock.ClockService;

public class LeaseItemTest_getCurrentValue {

    private final LocalDate now = LocalDate.now();

    private LeaseItem leaseItem;

    private LeaseTermImpl leaseTerm;

    private LocalDate getCurrentValueDateArgument;

    @Mock
    private ClockService mockClockService;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setUp() throws Exception {
        context.checking(new Expectations() {
            {
                oneOf(mockClockService).now();
                will(returnValue(now));
            }
        });

        leaseTerm = new LeaseTermImpl();
        leaseTerm.setValue(BigDecimal.TEN);

        leaseItem = new LeaseItem() {
            @Override
            @Hidden
            public LeaseTerm currentTerm(LocalDate date) {
                LeaseItemTest_getCurrentValue.this.getCurrentValueDateArgument = date;
                return leaseTerm;
            }
        };
        leaseItem.injectClockService(mockClockService);
    }

    @Test
    public void test() {
        assertThat(leaseItem.getTrialValue(), is(BigDecimal.TEN));
        assertThat(getCurrentValueDateArgument, is(now));
    }

}
