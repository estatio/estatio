package org.estatio.dom;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.services.clock.ClockService;

public class EstatioDomainObjectTest_injectClockService {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private ClockService mockClockService;

    static class SomeDomainObject extends EstatioDomainObject<SomeDomainObject> {
        public SomeDomainObject() {
            super(null);
        }
    }

    @Test
    public void testImpl() {
        final SomeDomainObject someDomainObject = new SomeDomainObject();
        someDomainObject.injectClockService(mockClockService);
        
        assertThat(someDomainObject.getClockService(), is(mockClockService));
    }

}
