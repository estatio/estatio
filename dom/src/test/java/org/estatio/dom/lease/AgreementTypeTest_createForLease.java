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

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.services.clock.ClockService;

public class AgreementTypeTest_createForLease {

    
    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DomainObjectContainer mockContainer;

    @Test
    public void test() {
        final Lease lease = new Lease();
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).newTransientInstance(Lease.class);
                will(returnValue(lease));
            }
        });
        final Agreement created = AgreementType.LEASE.create(mockContainer);
        assertThat(created, is((Agreement)lease));
        
        assertThat(created.getAgreementType(), is(AgreementType.LEASE));
    }

}
