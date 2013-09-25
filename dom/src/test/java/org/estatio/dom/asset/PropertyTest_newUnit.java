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

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class PropertyTest_newUnit {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private Units units;
    
    private Property property;

    
    @Before
    public void setup() {
        property = new Property();    
        property.setReference("ABC");
        property.injectUnits(units);
    }

    
    @Test
    public void newUnit() {
        final String unitRef = "ABC-123";
        final String unitName = "123";
        final UnitType unitType = UnitType.CINEMA;
        context.checking(new Expectations() {
            {
                oneOf(units).newUnit(unitRef, unitName, unitType);
            }
        });
        property.newUnit(unitRef, unitName, unitType);
    }

    @Test
    public void defaults() {
        assertThat(property.default0NewUnit(), is("ABC-000"));
        assertThat(property.default1NewUnit(), is("000"));
        assertThat(property.default2NewUnit(), is(UnitType.BOUTIQUE));
    }

}
