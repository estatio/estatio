package org.estatio.dom.asset;

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

public class PropertiesTest_newProperty {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DomainObjectContainer mockContainer;

    private Properties properties;

    @Before
    public void setup() {
        properties = new Properties();    
        properties.setContainer(mockContainer);
    }

    
    @Test
    public void newProperty() {
        final Property property = new Property();
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).newTransientInstance(Property.class);
                will(returnValue(property));
                
                oneOf(mockContainer).persistIfNotAlready(property);
            }
        });
        
        final Property newProperty = properties.newProperty("REF-1", "Name-1", PropertyType.CINEMA);
        assertThat(newProperty.getReference(), is("REF-1"));
        assertThat(newProperty.getName(), is("Name-1"));
        assertThat(newProperty.getPropertyType(), is(PropertyType.CINEMA));
    }

    @Test
    public void newProperty_withDefaultPropertyType() {
        final Property property = new Property();
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).newTransientInstance(Property.class);
                will(returnValue(property));
                
                oneOf(mockContainer).persistIfNotAlready(property);
            }
        });
        
        final Property newProperty = properties.newProperty("REF-1", "Name-1");
        assertThat(newProperty.getReference(), is("REF-1"));
        assertThat(newProperty.getName(), is("Name-1"));
        assertThat(newProperty.getPropertyType(), is(PropertyType.MIXED));
    }
}
