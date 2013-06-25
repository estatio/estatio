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

public class ChargeGroupsTest_newChargeGroup {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DomainObjectContainer mockContainer;

    private ChargeGroups chargeGroups;
    
    @Before
    public void setup() {
        chargeGroups = new ChargeGroups();    
        chargeGroups.setContainer(mockContainer);
    }

    
    @Test
    public void newChargeGroup() {
        final ChargeGroup chargeGroup = new ChargeGroup();
        
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).newTransientInstance(ChargeGroup.class);
                will(returnValue(chargeGroup));
                
                oneOf(mockContainer).persist(chargeGroup);
            }
        });
        
        final ChargeGroup newChargeGroup = chargeGroups.newChargeGroup("REF-1", "desc-1");
        assertThat(newChargeGroup.getReference(), is("REF-1"));
        assertThat(newChargeGroup.getDescription(), is("desc-1"));
    }
    
}