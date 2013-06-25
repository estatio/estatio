package org.estatio.dom.charge;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;

public class ChargesTest_newCharge {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DomainObjectContainer mockContainer;

    private Charges charges;

    private Charge existingCharge;
    
    @Before
    public void setup() {
        
        charges = new Charges() {
            @Override
            public Charge findChargeByReference(String reference) {
                return existingCharge;
            }
        };    
        charges.setContainer(mockContainer);
    }

    
    @Test
    public void newCharge_whenDoesNotExist() {
        final Charge charge = new Charge();
        
        existingCharge = null;

        context.checking(new Expectations() {
            {
                oneOf(mockContainer).newTransientInstance(Charge.class);
                will(returnValue(charge));
                
                oneOf(mockContainer).persist(charge);
            }
        });
        
        final Charge newCharge = charges.newCharge("REF-1");
        assertThat(newCharge.getReference(), is("REF-1"));
    }
    
    @Test
    public void newCharge_whenDoesExist() {
        existingCharge = new Charge();

        final Charge newCharge = charges.newCharge("REF-1");
        assertThat(newCharge, is(existingCharge));
    }

}
