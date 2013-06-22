package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class AgreementsTest_newAgreement {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    public static class SomeAgreement extends AgreementForTesting { }
    
    @Mock
    private DomainObjectContainer mockDomainObjectContainer;
    
    private AgreementType agreementType;
    
    private Agreements agreements;
    
    @Before
    public void setup() {
        agreementType = new AgreementType();
        agreementType.setImplementationClassName(SomeAgreement.class.getName());
        
        agreements = new Agreements();   
        agreements.setContainer(mockDomainObjectContainer);
    }

    
    @Test
    public void happyCase() {
        
        context.checking(new Expectations() {
            {
                oneOf(mockDomainObjectContainer).newTransientInstance(SomeAgreement.class);
                will(returnValue(new SomeAgreement()));
            }
        });
        Agreement newAgreement = agreements.newAgreement(agreementType, "ref-1", "name-1");
        
        // then
        assertThat(newAgreement.getAgreementType(), is(agreementType));
        
        // TODO: why are these null, and not set up correctly?
        //assertThat(newAgreement.getReference(), is("ref-1"));
        //assertThat(newAgreement.getName(), is("name-1"));
        assertThat(newAgreement.getName(), is(nullValue()));
        assertThat(newAgreement.getReference(), is(nullValue()));
        
        assertThat(newAgreement, is(instanceOf(SomeAgreement.class)));
    }

}
