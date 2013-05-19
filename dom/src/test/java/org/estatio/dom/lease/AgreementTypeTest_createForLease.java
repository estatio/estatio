package org.estatio.dom.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementType;

public class AgreementTypeTest_createForLease {

    
    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DomainObjectContainer mockContainer;

    @Test
    public void test() {
        final AgreementType agreementType = new AgreementType();
        agreementType.setImplementationClassName(Lease.class.getName());
        
        final Lease lease = new Lease();
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).newTransientInstance(Lease.class);
                will(returnValue(lease));
            }
        });
        final Agreement created = agreementType.create(mockContainer);
        assertThat(created, is((Agreement)lease));
        
        assertThat(created.getAgreementType(), is(agreementType));
    }

}
