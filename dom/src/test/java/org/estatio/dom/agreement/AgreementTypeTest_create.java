package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.log4j.jmx.Agent;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;

public class AgreementTypeTest_create {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Rule
    public ExpectedException exceptions = ExpectedException.none();
    
    private AgreementType agreementType;
    @Mock
    private DomainObjectContainer mockContainer;

    @Before
    public void setup() {
        agreementType = new AgreementType();
    }


    @Test
    public void happyCase() {
        // given
        agreementType.setImplementationClassName(AgreementForTesting.class.getName());
        
        // when
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).newTransientInstance(AgreementForTesting.class);
                will(returnValue(new AgreementForTesting()));
            }
        });
        final Agreement agreement = agreementType.create(mockContainer);
        
        // then
        assertThat(agreement.getAgreementType(), is(agreementType));
    }
    
    @Test
    public void badImplementationClassName() {
        exceptions.expectMessage("no.such.ClassName");
        exceptions.expect(ApplicationException.class);
        
        agreementType.setImplementationClassName("no.such.ClassName");
        
        // when
        context.checking(new Expectations() {
            {
                never(mockContainer);
            }
        });
        agreementType.create(mockContainer);
    }
    

}
