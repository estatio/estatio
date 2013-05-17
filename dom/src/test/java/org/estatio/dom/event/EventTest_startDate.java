package org.estatio.dom.event;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.services.clock.ClockService;

public class EventTest_startDate {

    private Event event;

    @Mock
    private ClockService mockClockService;

    private final LocalDate now = LocalDate.now();
    
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

        event = new Event();
        event.injectClockService(mockClockService);
    }
    
    @Test
    public void test() {
        assertThat(event.defaultStartDate(), is(now));
    }

}
