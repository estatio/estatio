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
package org.estatio.dom.budget;

import java.math.BigDecimal;

import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.dom.asset.Unit;

import static org.junit.Assert.assertEquals;

public class BudgetFoundationValueTypeTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    BigDecimal bigDecimal;

    @Test
    public void testEquals(){

        // when
        BudgetFoundationValueType budgetFoundationValueType = BudgetFoundationValueType.AREA;
        BudgetFoundationValueType budgetFoundationValueType2 = BudgetFoundationValueType.COUNT;
        BudgetFoundationValueType budgetFoundationValueType3 = BudgetFoundationValueType.MANUAL;
        Unit unit = new UnitForTesting();
        unit.setArea(bigDecimal);

        //then
        assertEquals(budgetFoundationValueType.valueOf(unit), bigDecimal);
        assertEquals(budgetFoundationValueType2.valueOf(unit), BigDecimal.ONE);
        assertEquals(budgetFoundationValueType3.valueOf(unit),null);
    }

}
