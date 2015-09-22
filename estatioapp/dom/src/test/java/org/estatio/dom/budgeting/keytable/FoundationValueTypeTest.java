/*
 * Copyright 2015 Yodo Int. Projects and Consultancy
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.estatio.dom.budgeting.keytable;

import java.math.BigDecimal;

import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.UnitForTesting;

import static org.junit.Assert.assertEquals;

public class FoundationValueTypeTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    BigDecimal bigDecimal;

    @Test
    public void testEquals(){

        // when
        FoundationValueType foundationValueType = FoundationValueType.AREA;
        FoundationValueType foundationValueType2 = FoundationValueType.COUNT;
        FoundationValueType foundationValueType3 = FoundationValueType.MANUAL;
        Unit unit = new UnitForTesting();
        unit.setArea(bigDecimal);

        //then
        assertEquals(foundationValueType.valueOf(unit), bigDecimal);
        assertEquals(foundationValueType2.valueOf(unit), BigDecimal.ONE);
        assertEquals(foundationValueType3.valueOf(unit),null);
    }

}
