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

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.asset.Unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by jodo on 22/04/15.
 */
public class BudgetKeyItemTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final BudgetKeyItem pojo = new BudgetKeyItem();
            newPojoTester()
                    .withFixture(pojos(BudgetKeyTable.class, BudgetKeyTableForTesting.class))
                    .withFixture(pojos(Unit.class, UnitForTesting.class))
                    .exercise(pojo);
        }

    }

    @Test
    public void testChangeKeyValue(){

        // given
        BudgetKeyTable table = new BudgetKeyTable();
        table.setNumberOfDigits(3);
        BudgetKeyItem item = new BudgetKeyItem();
        item.setValue(new BigDecimal(2));
        item.setBudgetKeyTable(table);
        assertTrue(item.getValue().equals(new BigDecimal(2)));

        //when
        item.changeValue(new BigDecimal(2.3335));

        //then
        assertEquals(item.getValue(),new BigDecimal(2.333).setScale(3, BigDecimal.ROUND_HALF_UP));
        assertFalse(item.getValue().equals(new BigDecimal(2.334).setScale(3, BigDecimal.ROUND_HALF_UP)));

    }

    @Test
    public void testValidateChangeKeyValue(){

        // given
        BudgetKeyTable table = new BudgetKeyTable();
        table.setNumberOfDigits(3);
        BudgetKeyItem item = new BudgetKeyItem();
        item.setValue(new BigDecimal(2));
        item.setBudgetKeyTable(table);
        assertTrue(item.getValue().equals(new BigDecimal(2)));

        //when, then
        assertEquals(item.validateChangeValue(new BigDecimal(-0.001)),"Value cannot be less than zero");
    }

    @Test
    public void testChangeSourceValue(){

        // given
        BudgetKeyTable table = new BudgetKeyTable();
        table.setNumberOfDigits(3);
        BudgetKeyItem item = new BudgetKeyItem();
        item.setSourceValue(new BigDecimal(2));
        item.setBudgetKeyTable(table);
        assertTrue(item.getSourceValue().equals(new BigDecimal(2)));

        //when
        item.changeSourceValue(new BigDecimal(2.335));

        //then
        assertEquals(item.getSourceValue(),new BigDecimal(2.33).setScale(2, BigDecimal.ROUND_HALF_UP));
        assertFalse(item.getSourceValue().equals(new BigDecimal(2.34).setScale(2, BigDecimal.ROUND_HALF_UP)));

    }

    @Test
    public void testValidateChangeSourceValue(){

        // given
        BudgetKeyTable table = new BudgetKeyTable();
        table.setNumberOfDigits(3);
        BudgetKeyItem item = new BudgetKeyItem();
        item.setSourceValue(new BigDecimal(2));
        item.setBudgetKeyTable(table);
        assertTrue(item.getSourceValue().equals(new BigDecimal(2)));

        //when, then
        assertEquals(item.validateChangeSourceValue(new BigDecimal(-0.001)),"Source Value must be positive");
    }

}
