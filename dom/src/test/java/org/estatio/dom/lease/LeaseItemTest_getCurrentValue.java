package org.estatio.dom.lease;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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

        leaseItem = new LeaseItem() {

            @Override
            @Hidden
            public BigDecimal valueForDate(LocalDate date) {
                LeaseItemTest_getCurrentValue.this.getCurrentValueDateArgument = date;
                return BigDecimal.TEN;
            }
        };
        leaseItem.injectClockService(mockClockService);
    }
    
    @Test
    public void test() {
        assertThat(leaseItem.getCurrentValue(), is(BigDecimal.TEN));
        assertThat(getCurrentValueDateArgument, is(now));
    }

}
