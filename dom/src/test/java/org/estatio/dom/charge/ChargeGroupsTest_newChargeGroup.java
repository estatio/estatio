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
package org.estatio.dom.charge;

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
        
        final ChargeGroup newChargeGroup = chargeGroups.createChargeGroup("REF-1", "desc-1");
        assertThat(newChargeGroup.getReference(), is("REF-1"));
        assertThat(newChargeGroup.getDescription(), is("desc-1"));
    }
    
}