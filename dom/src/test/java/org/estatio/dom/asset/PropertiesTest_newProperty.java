/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.asset;

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
        
        final Property newProperty = properties.newProperty("REF-1", "Name-1", PropertyType.CINEMA, null, null, null);
        assertThat(newProperty.getReference(), is("REF-1"));
        assertThat(newProperty.getName(), is("Name-1"));
        assertThat(newProperty.getType(), is(PropertyType.CINEMA));
    }
    
    @Test
    public void defaults() {
        assertThat(properties.default2NewProperty(), is(PropertyType.MIXED));
    }

}
