/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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

public class UnitsTest_newUnit {

    static class UnitForTesting extends Unit {}
    
    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DomainObjectContainer mockContainer;

    private Units<UnitForTesting> units;

    
    @Before
    public void setup() {
        units = new Units<UnitForTesting>(UnitForTesting.class){};    
        units.setContainer(mockContainer);
    }

    
    @Test
    public void newUnit() {
        final UnitForTesting unit = new UnitForTesting();
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).newTransientInstance(UnitForTesting.class);
                will(returnValue(unit));
                
                oneOf(mockContainer).persist(unit);
            }
        });
        
        final Unit newUnit = units.newUnit("REF-1", "Name-1", UnitType.EXTERNAL);
        assertThat(newUnit.getReference(), is("REF-1"));
        assertThat(newUnit.getName(), is("Name-1"));
        assertThat(newUnit.getType(), is(UnitType.EXTERNAL));
    }

}
