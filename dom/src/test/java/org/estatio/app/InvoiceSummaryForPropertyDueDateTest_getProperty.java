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
package org.estatio.app;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDate;

public class InvoiceSummaryForPropertyDueDateTest_getProperty {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private Properties mockProperties;

    private Property property;
    
    private InvoiceSummaryForPropertyDueDate summary;

    @Before
    public void setUp() throws Exception {
        property = new Property();
        property.setReference("REF-1");
        
        summary = new InvoiceSummaryForPropertyDueDate();
        summary.setReference("REF-1");
        summary.injectProperties(mockProperties);
    }
    
    @Test
    public void whenEagerlySet() {
        summary.setProperty(property);
        context.checking(new Expectations() {
            {
                never(mockProperties);
            }
        });
        assertThat(summary.getProperty(), is(property));
    }
    
    @Test
    public void whenLazilyLoaded() {
        context.checking(new Expectations() {
            {
                oneOf(mockProperties).findPropertyByReference("REF-1");
                will(returnValue(property));
            }
        });
        
        assertThat(summary.getProperty(), is(property));
    }
    
}
